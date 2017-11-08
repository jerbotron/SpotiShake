package com.jerbotron_mac.spotisave.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import static com.jerbotron_mac.spotisave.data.SongInfo.CREATE_TB_SONG_HISTORY;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "SpotiShake_database";
    public static String TB_SONG_HISTORY = "song_history_table";
    public static int DB_VERSION = 1;

    DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TB_SONG_HISTORY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLiteException {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TB_SONG_HISTORY);
            onCreate(db);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
