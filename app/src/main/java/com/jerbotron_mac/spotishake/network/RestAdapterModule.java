package com.jerbotron_mac.spotishake.network;

import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module
public class RestAdapterModule {

    private static final String SPOTIFY_ACCOUNTS_ENDPOINT = "https://accounts.spotify.com";

    @Provides
    @ApplicationScope
    @Named("spotify")
    public RestAdapter provideSpotifyRestAdapter(RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    @Provides
    @ApplicationScope
    @Named("spotify-auth")
    public RestAdapter provideSpotifyAuthRestAdapter(RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(SPOTIFY_ACCOUNTS_ENDPOINT)
                .setRequestInterceptor(requestInterceptor)
                .build();
    }

    @Provides
    @ApplicationScope
    public RequestInterceptor provideRequestInterceptor(final SharedUserPrefs sharedUserPrefs) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Bearer " + sharedUserPrefs.getSpotifyAuthToken());
            }
        };
    }
}
