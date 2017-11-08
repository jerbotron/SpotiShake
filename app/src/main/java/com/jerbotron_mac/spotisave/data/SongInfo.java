package com.jerbotron_mac.spotisave.data;

import android.provider.BaseColumns;

import static com.jerbotron_mac.spotisave.data.DatabaseHelper.TB_SONG_HISTORY;

public class SongInfo implements BaseColumns {

    public static String TRACK_TITLE = "track_title";
    public static String TRACK_ALBUM = "track_album";
    public static String TRACK_ARTIST = "track_artist";
    public static String TRACK_NUMBER = "track_number";
    public static String COVER_ART_URL = "cover_art_url";
    public static String GENRE = "genre";
    public static String TIMESTAMP_MS = "timestamp_ms";

    public final static String CREATE_TB_SONG_HISTORY =
            "CREATE TABLE IF NOT EXISTS " + TB_SONG_HISTORY + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TRACK_TITLE + " TEXT, " +
            TRACK_ALBUM + " TEXT, " +
            TRACK_ARTIST + " TEXT, " +
            COVER_ART_URL + " TEXT, "  +
            GENRE + " TEXT, "  +
            TRACK_NUMBER + " INTEGER, "  +
            TIMESTAMP_MS + " INTEGER "  +
            ")";

    private String title;
    private String album;
    private String artist;
    private String coverArtUrl;
    private String genre;
    private int trackNumber;
    private long timestampMs;

    public SongInfo(String songId,
                    String title,
                    String album,
                    String artist,
                    String coverArtUrl,
                    String genre,
                    int trackNumber,
                    long timestampMs) {
        this.album = album;
        this.title = title;
        this.artist = artist;
        this.coverArtUrl = coverArtUrl;
        this.genre = genre;
        this.trackNumber = trackNumber;
        this.timestampMs = timestampMs;
    }
}
