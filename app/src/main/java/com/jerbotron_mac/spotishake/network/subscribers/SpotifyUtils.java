package com.jerbotron_mac.spotishake.network.subscribers;

import com.jerbotron_mac.spotishake.network.TrackData;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class SpotifyUtils {

    public static boolean isSameTrack(Track track, TrackData trackData) {
        String longer = (track.name.length() > trackData.getTrack().length()) ? track.name : trackData.getTrack();
        String shorter = (longer.equals(track.name)) ? trackData.getTrack() : track.name;
        if (!longer.contains(shorter)) {
            return false;
        }
        for (ArtistSimple artist : track.artists) {
            longer = (artist.name.length() > trackData.getArtist().length()) ? artist.name : trackData.getArtist();
            shorter = (longer.equals(artist.name)) ? trackData.getArtist() : artist.name;
            if (longer.contains(shorter)) {
                return true;
            }
        }
        return false;
    }

}
