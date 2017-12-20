package com.jerbotron_mac.spotishake.network;

import android.util.Log;
import android.widget.Toast;

import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TracksPager;
import kaaes.spotify.webapi.android.models.UserPrivate;



import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;

public class SpotifyServiceWrapper {

    private SpotifyApi spotifyApi;
    private SpotifyService spotifyService;
    private RestAdapter restAdapter;
    private AppUtils appUtils;

    public SpotifyServiceWrapper(SharedUserPrefs sharedUserPrefs, AppUtils appUtils) {
        this.appUtils = appUtils;
        spotifyApi = new SpotifyApi();

        String accessToken = sharedUserPrefs.getSpotifyAuthToken();

        if (accessToken != null && !accessToken.equals("")) {
            setAccessToken(accessToken);
        }
    }

    public void setAccessToken(String accessToken) {
        spotifyApi.setAccessToken(accessToken);
        setupRestAdapter(accessToken);
    }

    private void setupRestAdapter(final String accessToken) {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + accessToken);
                    }
                }).build();

        spotifyService = restAdapter.create(SpotifyService.class);
    }

    public void getUserProfile(SpotifyCallback<UserPrivate> callback) {
        spotifyService.getMe(callback);
    }

    public void saveTrackToSavedSongs(TrackData trackData) {
        searchTrack(trackData, new SearchTrackCallback());
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

    private class SearchTrackCallback extends SpotifyCallback<TracksPager> {

        private SearchTrackCallback() {
        }

        @Override
        public void failure(SpotifyError spotifyError) {
            spotifyError.printStackTrace();
        }

        @Override
        public void success(TracksPager tracksPager, Response response) {
            if (tracksPager != null) {
                String trackId = tracksPager.tracks.items.get(0).id;
                containsTrack(trackId, new ContainsTrackCallback(trackId));
            }
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
