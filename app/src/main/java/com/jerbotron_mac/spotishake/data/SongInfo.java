package com.jerbotron_mac.spotishake.data;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnContentType;
import com.gracenote.gnsdk.GnDataLevel;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnTrack;
import com.jerbotron_mac.spotishake.shared.MaterialColor;
import com.jerbotron_mac.spotishake.utils.AppUtils;

import java.util.HashMap;
import java.util.Map;

import static com.jerbotron_mac.spotishake.data.DatabaseHelper.TB_SONG_HISTORY;

public class SongInfo implements BaseColumns {

    // these 3 keys must match Spotify query key, do not change value
    public static String TRACK_TITLE = "track";
    public static String TRACK_ARTIST = "artist";
    public static String TRACK_ALBUM = "album";

    public static String TRACK_GENRE = "genre";
    public static String COVER_ART_URL = "cover_art_url";
    public static String SPOTIFY_ID = "spotify_id";
    public static String TIMESTAMP_MS = "timestamp_ms";
    public static String CARD_COLOR = "card_color";

    public final static String CREATE_TB_SONG_HISTORY =
            "CREATE TABLE IF NOT EXISTS " + TB_SONG_HISTORY + "(" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TRACK_TITLE + " TEXT, " +
                    TRACK_ALBUM + " TEXT, " +
                    TRACK_ARTIST + " TEXT, " +
                    TRACK_GENRE + " TEXT, " +
                    COVER_ART_URL + " TEXT, "  +
                    SPOTIFY_ID + " TEXT, "  +
                    CARD_COLOR  + " INTEGER, " +
                    TIMESTAMP_MS + " INTEGER "  +
                    ")";

    private String title = "";
    private String album = "";
    private String artist = "";
    private String genre = "";
    private String coverArtUrl = "";
    private String spotifyId = "";
    private long timestampMs;
    private int cardColor;

    public SongInfo(GnResponseAlbums results) {
        try {
            GnAlbumIterator iter = results.albums().getIterator();
            // todo: potentially iterate through all results and pick most accurate data
            extractAlbumData(iter.next());
            this.timestampMs = System.currentTimeMillis();
            this.cardColor = MaterialColor.getRandomColor();
        } catch (GnException e) {
            e.printStackTrace();
        }
    }

    public SongInfo(String title, String artist, String album) {
        this.title = title;
        this.artist = artist;
        this.album = album;
    }

    private void extractAlbumData(GnAlbum album) {
        this.album = album.title().display();

        GnTrack track = album.trackMatched();
        // try using the matched track to populate fields first
        if (track != null) {
            this.title = track.title().display();
            this.artist = track.artist().name().display();
            this.genre = track.genre(GnDataLevel.kDataLevel_1);
            this.coverArtUrl = track.content(GnContentType.kContentTypeImageCover).asset(GnImageSize.kImageSizeLarge).url();
        }

        //use album data if needed
        if (AppUtils.isStringEmpty(this.title)) {
            this.title = album.title().display();
        }
        if (AppUtils.isStringEmpty(this.artist)) {
            this.artist = album.artist().name().display();
        }
        if (AppUtils.isStringEmpty(this.genre)) {
            this.genre = album.genre(GnDataLevel.kDataLevel_1);
        }
        if (AppUtils.isStringEmpty(this.coverArtUrl)) {
            this.coverArtUrl = album.coverArt().asset(GnImageSize.kImageSizeLarge).url();
        }
    }

    @NonNull
    public Map<String, Object> getQueryMap() {
        Map<String, Object> queryMap = new HashMap<>();
        if (title != null) {
            queryMap.put(TRACK_TITLE, title);
        }
        if (artist != null) {
            queryMap.put(TRACK_ARTIST, artist);
        }
        if (album != null) {
            queryMap.put(TRACK_ALBUM, album);
        }
        return queryMap;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACK_TITLE, title);
        contentValues.put(TRACK_ALBUM, album);
        contentValues.put(TRACK_ARTIST, artist);
        contentValues.put(TRACK_GENRE, genre);
        contentValues.put(COVER_ART_URL, coverArtUrl);
        contentValues.put(SPOTIFY_ID, spotifyId);
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

    public String getGenre() {
        return genre;
    }

    public String getCoverArtUrl() {
        return coverArtUrl;
    }

    public String getSpotifyId() {
        return spotifyId;
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

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }
}
