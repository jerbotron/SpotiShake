package com.jerbotron_mac.spotishake.network;

import android.widget.Toast;

import com.jerbotron_mac.spotishake.data.SongInfo;
import com.jerbotron_mac.spotishake.utils.AppUtils;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
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

    public void searchTrack(SongInfo songInfo, SpotifyCallback<TracksPager> callback) {
        spotifyService.searchTracks(songInfo.getTitle(), songInfo.getQueryMap(), callback);
    }

    // Check if user has already saved this track, don't save again if it's already saved
    public void saveTrackIfApplicable(String trackId) {
        containsTrack(trackId, new ContainsTrackCallback(trackId));
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

    public class ContainsTrackCallback extends SpotifyCallback<boolean[]> {

        private String trackId;

        private ContainsTrackCallback(String trackId) {
            this.trackId = trackId;
        }

        @Override
        public void failure(SpotifyError spotifyError) {
            spotifyError.printStackTrace();
            appUtils.showToast("Failed to save to Spotify", Toast.LENGTH_SHORT);
        }

        @Override
        public void success(boolean[] booleans, Response response) {
            if (booleans.length > 0 && !booleans[0]) {
                saveTrack(trackId, new SpotifyCallback<Object>() {
                    @Override
                    public void failure(SpotifyError spotifyError) {
                        spotifyError.printStackTrace();
                        appUtils.showToast("Failed to save to Spotify", Toast.LENGTH_SHORT);
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
