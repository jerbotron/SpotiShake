package com.jerbotron_mac.spotishake.network;

import android.widget.Toast;

import com.jerbotron_mac.spotishake.network.subscribers.SpotifyUtils;
import com.jerbotron_mac.spotishake.utils.AppUtils;

import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ErrorDetails;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;



import retrofit.client.Response;

public class SpotifyServiceWrapper {

    private SpotifyService spotifyService;
    private SpotifyAuthService authService;
    private AppUtils appUtils;

    public SpotifyServiceWrapper(SpotifyService spotifyService, AppUtils appUtils) {
        this.spotifyService = spotifyService;
        this.appUtils = appUtils;
    }

    public void getUserProfile(SpotifyCallback<UserPrivate> callback) {
        spotifyService.getMe(callback);
    }

    public void saveTrackToSpotify(TrackData trackData) {
        searchTrack(trackData, new SearchTrackCallback(trackData));
    }

    public void searchTrack(TrackData trackData, SpotifyCallback<TracksPager> callback) {
        String query = trackData.getTrack();
        Map<String, Object> queryMap = trackData.getDataMap();
        if (queryMap != null && queryMap.size() > 0) {
            spotifyService.searchTracks(query, queryMap, callback);
        } else {
            spotifyService.searchTracks(query, callback);
        }
    }

    public void containsTrack(String trackId, SpotifyCallback<boolean[]> callback) {
        spotifyService.containsMySavedTracks(trackId, callback);
    }

    public void saveTrack(String trackId, SpotifyCallback<Object> callback) {
        spotifyService.addToMySavedTracks(trackId, callback);
    }

    public void getUser(SpotifyCallback<UserPrivate> callback) {
        spotifyService.getMe(callback);
    }

    private class SearchTrackCallback extends SpotifyCallback<TracksPager> {

        private TrackData trackData;

        public SearchTrackCallback(TrackData trackData) {
            this.trackData = trackData;
        }

        @Override
        public void failure(SpotifyError spotifyError) {
            spotifyError.printStackTrace();
        }

        @Override
        public void success(TracksPager tracksPager, Response response) {
            if (tracksPager != null) {
                for (Track track : tracksPager.tracks.items) {
                    if (SpotifyUtils.isSameTrack(track, trackData)) {
                        containsTrack(track.id, new ContainsTrackCallback(track.id));
                        return;
                    }
                }
            }
            appUtils.showToast("Could not find song in Spotify", Toast.LENGTH_SHORT);
        }
    }

    private class ContainsTrackCallback extends SpotifyCallback<boolean[]> {

        private String trackId;

        private ContainsTrackCallback(String trackId) {
            this.trackId = trackId;
        }

        @Override
        public void failure(SpotifyError spotifyError) {
            spotifyError.printStackTrace();
        }

        @Override
        public void success(boolean[] booleans, Response response) {
            if (booleans.length > 0 && !booleans[0]) {
                saveTrack(trackId, new SpotifyCallback<Object>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        spotifyError.printStackTrace();
                    }

                    @Override
                    public void success(Object o, Response response) {
                        appUtils.showToast("Saved song to Spotify", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                appUtils.showToast("Song already saved in Spotify", Toast.LENGTH_SHORT);
            }
        }
    }
}
