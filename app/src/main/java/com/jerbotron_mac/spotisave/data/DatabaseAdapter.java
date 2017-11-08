/* Gracenote Android Music SDK Sample Application
 *
 * Copyright (C) 2010 Gracenote, Inc. All Rights Reserved.
 */
package com.jerbotron_mac.spotisave.data;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.widget.*;

import com.gracenote.gnsdk.*;

import static android.provider.BaseColumns._ID;
import static com.jerbotron_mac.spotisave.data.DatabaseHelper.TB_SONG_HISTORY;
import static com.jerbotron_mac.spotisave.data.SongInfo.COVER_ART_URL;
import static com.jerbotron_mac.spotisave.data.SongInfo.GENRE;
import static com.jerbotron_mac.spotisave.data.SongInfo.TIMESTAMP_MS;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_ALBUM;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_ARTIST;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_NUMBER;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_TITLE;


/**
 * <p>
 * This class will work as a utility class to deal with database. It will be
 * responsible for database connection, insert a row into database, retrieve the
 * cursor for select and update operations.
 * 
 * 
 */
public final class DatabaseAdapter {

    private final Context context;

	private DatabaseHelper databaseHelper;
	private SQLiteDatabase db;
	private static final int MAX_COUNT = 1000;

	public DatabaseAdapter(Context context) {
		this.context = context;
		databaseHelper = new DatabaseHelper(this.context);
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
        ContentValues values = new ContentValues();
        long currentTimeMs = System.currentTimeMillis();

        values.put(TIMESTAMP_MS, currentTimeMs);
        try {
            GnAlbumIterator iter = gnAlbums.albums().getIterator();
            while (iter.hasNext()) {
                GnAlbum album = iter.next();
                if (album.title().display() != null) {
                    values.put(TRACK_ALBUM, album.title().display());
                }

                if (album.trackMatched() != null) {
                    String trackArtist = album.trackMatched().artist().name().display();
                    if (trackArtist == null || trackArtist.isEmpty()) {
                        //use album artist if track artist not available
                        trackArtist = album.artist().name().display();
                    }
                    values.put(TRACK_ARTIST, trackArtist);
                    values.put(TRACK_TITLE, album.trackMatched().title().display());
                }

                values.put(COVER_ART_URL, album.coverArt().asset(GnImageSize.kImageSizeLarge).url());
                values.put(TRACK_NUMBER, album.trackMatchNumber());

                long result = db.insert(TB_SONG_HISTORY, null, values);
                if (result == -1) {
                    throw new SQLException("Error calling db.insert()");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to insert row: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

	public int deleteRow(String rowId) {
		int result = 0;
		try {
			result = db.delete(TB_SONG_HISTORY, _ID + " =?", new String[]{rowId});
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(context, "Failed to delete record: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public int deleteAll() {
		int result = 0;
		try {
			result = db.delete(TB_SONG_HISTORY, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(context, "Failed to delete all records: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	public Cursor getCursor() {
		Cursor cursor = null;
		try {
            String[] columns = {
                    _ID,
                    TRACK_TITLE,
                    TRACK_ALBUM,
                    TRACK_ARTIST,
                    TRACK_NUMBER,
                    COVER_ART_URL,
                    GENRE,
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
			Toast.makeText(context, "Failed to retrieve cursor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return cursor;
	}
}
