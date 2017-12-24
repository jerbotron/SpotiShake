package com.jerbotron_mac.spotishake.network.subscribers;

import com.jerbotron_mac.spotishake.data.SongInfo;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class SpotifyUtils {

    public static boolean isSameTrack(Track track, SongInfo songInfo) {
        String longer = (track.name.length() > songInfo.getTitle().length()) ? track.name : songInfo.getTitle();
        String shorter = (longer.equals(track.name)) ? songInfo.getTitle() : track.name;
        if (!longer.contains(shorter)) {
            return false;
        }
        for (ArtistSimple artist : track.artists) {
            longer = (artist.name.length() > songInfo.getArtist().length()) ? artist.name : songInfo.getArtist();
            shorter = (longer.equals(artist.name)) ? songInfo.getArtist() : artist.name;
            if (longer.contains(shorter)) {
                return true;
            }
        }
        return false;
    }

}
