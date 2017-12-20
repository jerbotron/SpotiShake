package com.jerbotron_mac.spotishake.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.Log;
import android.widget.*;

import com.gracenote.gnsdk.*;
import com.jerbotron_mac.spotishake.utils.AppUtils;

import static android.provider.BaseColumns._ID;
import static com.jerbotron_mac.spotishake.data.DatabaseHelper.TB_SONG_HISTORY;
import static com.jerbotron_mac.spotishake.data.SongInfo.CARD_COLOR;
import static com.jerbotron_mac.spotishake.data.SongInfo.COVER_ART_URL;
import static com.jerbotron_mac.spotishake.data.SongInfo.TIMESTAMP_MS;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_ALBUM;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_ARTIST;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_TITLE;


/**
 * This class will work as a utility class to deal with database. It will be
 * responsible for database connection, insert a row into database, retrieve the
 * cursor for select and update operations.
 */
public final class DatabaseAdapter {

    private AppUtils appUtils;
	private DatabaseHelper databaseHelper;
	private SQLiteDatabase db;

	public DatabaseAdapter(Context context, AppUtils appUtils) {
	    this.appUtils = appUtils;
		databaseHelper = new DatabaseHelper(context);
        open();
	}

	public DatabaseAdapter open() throws SQLException {
		db = databaseHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		databaseHelper.close();
	}

    public void insertChanges(GnResponseAlbums gnAlbums) throws GnException {

        SongInfo songInfo = new SongInfo(gnAlbums);

        if (doesRowExist(songInfo)) {
            Log.d(getClass().getName(), "song already exists in table");
            return;
        }

        ContentValues values = songInfo.getContentValues();
        try {
            long result = db.insert(TB_SONG_HISTORY, null, values);
            if (result == -1) {
                throw new SQLException("Error calling db.insert()");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            appUtils.showToast("Failed to insert row: "+ e.getMessage(), Toast.LENGTH_SHORT);
        }
    }

	public void deleteRow(String rowId) {
		try {
			db.delete(TB_SONG_HISTORY, _ID + " = ?", new String[]{rowId});
		} catch (SQLException e) {
			e.printStackTrace();
            appUtils.showToast("Failed to delete record: " + e.getMessage(), Toast.LENGTH_SHORT);
		}
	}

	public void deleteAll() {
		try {
			db.delete(TB_SONG_HISTORY, null, null);
            appUtils.showToast("History deleted", Toast.LENGTH_SHORT);
		} catch (SQLException e) {
			e.printStackTrace();
            appUtils.showToast("Failed to delete all records: " + e.getMessage(), Toast.LENGTH_SHORT);
		}
	}

	public Cursor getCursor() {
		Cursor cursor = null;
		try {
            String[] columns = {
                    _ID,
                    TRACK_TITLE,
                    TRACK_ALBUM,
                    TRACK_ARTIST,
                    COVER_ART_URL,
                    CARD_COLOR,
                    TIMESTAMP_MS
            };

            String orderBy = TIMESTAMP_MS + " DESC";

			cursor = db.query(
			        TB_SONG_HISTORY,
                    columns,
                    null,
                    null,
                    null,
                    null,
                    orderBy);
		} catch (SQLException e) {
			e.printStackTrace();
            appUtils.showToast("Failed to retrieve cursor: " + e.getMessage(), Toast.LENGTH_SHORT);
		}
		return cursor;
	}

	private boolean doesRowExist(SongInfo songInfo) {
        Cursor cursor;
        boolean doesExist = false;
        try {
            String[] columns = {
                    _ID,
                    TRACK_TITLE,
                    TRACK_ALBUM,
                    TRACK_ARTIST,
                    COVER_ART_URL,
                    CARD_COLOR
            };

            String whereClause =
                    TRACK_TITLE + " = ? AND " +
                    TRACK_ALBUM + " = ? AND " +
                    TRACK_ARTIST + " = ? AND " +
                    COVER_ART_URL + " = ?";

            String[] whereArgs = new String[] {
                    songInfo.getTitle(),
                    songInfo.getAlbum(),
                    songInfo.getArtist(),
                    songInfo.getCoverArtUrl()
            };

            String orderBy = TIMESTAMP_MS + " ASC";

            cursor = db.query(
                    TB_SONG_HISTORY,
                    columns,
                    whereClause,
                    whereArgs,
                    null,
                    null,
                    orderBy);

            if (cursor.getCount() >= 1) {
                doesExist = true;
                cursor.moveToFirst();
                // keep the same card color
                songInfo.setCardColor(cursor.getInt(cursor.getColumnIndexOrThrow(CARD_COLOR)));
                while (cursor.moveToNext()) {
                    deleteRow(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(_ID))));
                }
                // update timestamp on current song
                db.update(TB_SONG_HISTORY, songInfo.getContentValues(), whereClause, whereArgs);
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doesExist;
    }
}
