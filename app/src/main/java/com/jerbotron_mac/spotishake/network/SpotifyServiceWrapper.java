package com.jerbotron_mac.spotishake.network;

import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

public class SpotifyServiceWrapper {

    private SpotifyApi spotifyApi;
    private SpotifyService spotifyService;
    private RestAdapter restAdapter;

    public SpotifyServiceWrapper(SharedUserPrefs sharedUserPrefs) {
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

//    public void searchTrack() {
//        spotifyService.searchTracks();
//    }
}
