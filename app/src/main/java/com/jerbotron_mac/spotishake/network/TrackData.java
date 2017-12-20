package com.jerbotron_mac.spotishake.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jerbotron-mac on 12/20/17.
 */

public class TrackData {

    private final static String ALBUM = "album";
    private final static String ARTIST = "artist";
    private final static String PLAYLIST = "playlist";
    private final static String TRACK = "track";

    private Map<String, Object> dataMap = new HashMap<>();

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public String getTrack() {
        return dataMap.get(TRACK).toString();
    }

    private void setAlbum(String album) {
        if (album != null) {
            dataMap.put(ALBUM, album);
        }
    }

    private void setArtist(String artist) {
        if (artist != null) {
            dataMap.put(ARTIST, artist);
        }
    }

    private void setPlaylist(String playlist) {
        if (playlist != null) {
            dataMap.put(PLAYLIST, playlist);
        }
    }

    private void setTrack(String track) {
        if (track != null) {
            dataMap.put(TRACK, track);
        }
    }

    public static class Builder {
        String album;
        String artist;
        String playlist;
        String track;

        public TrackData.Builder setAlbum(String album) {
            this.album = album;
            return this;
        }

        public TrackData.Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public TrackData.Builder setPlaylist(String playlist) {
            this.playlist = playlist;
            return this;
        }

        public TrackData.Builder setTrack(String track) {
            this.track = track;
            return this;
        }

        public TrackData build() {
            TrackData data = new TrackData();
            data.setAlbum(album);
            data.setArtist(artist);
            data.setPlaylist(playlist);
            data.setTrack(track);
            return data;
        }
    }
}
