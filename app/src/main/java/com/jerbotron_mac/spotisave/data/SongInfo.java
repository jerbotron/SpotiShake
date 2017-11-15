package com.jerbotron_mac.spotisave.data;

import android.content.ContentValues;
import android.provider.BaseColumns;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.jerbotron_mac.spotisave.shared.MaterialColor;

import static com.jerbotron_mac.spotisave.data.DatabaseHelper.TB_SONG_HISTORY;

public class SongInfo implements BaseColumns {

    public static String TRACK_TITLE = "track_title";
    public static String TRACK_ALBUM = "track_album";
    public static String TRACK_ARTIST = "track_artist";
    public static String COVER_ART_URL = "cover_art_url";
    public static String TIMESTAMP_MS = "timestamp_ms";
    public static String CARD_COLOR = "card_color";

    public final static String CREATE_TB_SONG_HISTORY =
            "CREATE TABLE IF NOT EXISTS " + TB_SONG_HISTORY + "(" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TRACK_TITLE + " TEXT, " +
            TRACK_ALBUM + " TEXT, " +
            TRACK_ARTIST + " TEXT, " +
            COVER_ART_URL + " TEXT, "  +
            CARD_COLOR  + " INTEGER, " +
            TIMESTAMP_MS + " INTEGER "  +
            ")";

    private String title;
    private String album;
    private String artist;
    private String coverArtUrl;
    private long timestampMs;
    private int cardColor;

    public SongInfo(String title,
                    String album,
                    String artist,
                    String coverArtUrl,
                    long timestampMs,
                    int cardColor) {
        this.album = album;
        this.title = title;
        this.artist = artist;
        this.coverArtUrl = coverArtUrl;
        this.timestampMs = timestampMs;
        this.cardColor = cardColor;
    }

    public SongInfo(GnResponseAlbums gnAlbums) {
        try {
            GnAlbumIterator iter = gnAlbums.albums().getIterator();
            while (iter.hasNext()) {
                GnAlbum album = iter.next();
                if (album.title().display() != null) {
                    this.album = album.title().display();
                }

                if (album.trackMatched() != null) {
                    String trackArtist = album.trackMatched().artist().name().display();
                    if (trackArtist == null || trackArtist.isEmpty()) {
                        //use album artist if track artist not available
                        trackArtist = album.artist().name().display();
                    }
                    this.artist = trackArtist;
                    this.title = album.trackMatched().title().display();
                }

                this.coverArtUrl = album.coverArt().asset(GnImageSize.kImageSizeLarge).url();
            }
            this.timestampMs = System.currentTimeMillis();
            this.cardColor = MaterialColor.getRandomColor();
        } catch (GnException e) {
            e.printStackTrace();
        }
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACK_ALBUM, album);
        contentValues.put(TRACK_TITLE, title);
        contentValues.put(TRACK_ARTIST, artist);
        contentValues.put(COVER_ART_URL, coverArtUrl);
        contentValues.put(TIMESTAMP_MS, timestampMs);
        contentValues.put(CARD_COLOR, cardColor);
        return contentValues;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getCoverArtUrl() {
        return coverArtUrl;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int cardColor) {
        this.cardColor = cardColor;
    }

    public long getTimestampMs() {
        return timestampMs;
    }
}
