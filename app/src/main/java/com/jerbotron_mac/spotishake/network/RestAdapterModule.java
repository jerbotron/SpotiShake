package com.jerbotron_mac.spotishake.network;

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.shared.AppConstants;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import kaaes.spotify.webapi.android.SpotifyApi;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module
public class RestAdapterModule {

    private static final String SPOTIFY_ACCOUNTS_ENDPOINT = "https://accounts.spotify.com";

    @Provides
    @ApplicationScope
    @Named("spotify")
    public RestAdapter provideSpotifyRestAdapter(@Named("standard") RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(new Gson()))
                .build();
    }

    @Provides
    @ApplicationScope
    @Named("spotify-auth")
    public RestAdapter provideSpotifyAuthRestAdapter(@Named("auth") RequestInterceptor requestInterceptor) {
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                return chain.proceed(request);
            }
        });

        return new RestAdapter.Builder()
                .setEndpoint(SPOTIFY_ACCOUNTS_ENDPOINT)
                .setClient(new OkClient(client))
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(new Gson()))
                .build();
    }

    @Provides
    @ApplicationScope
    @Named("standard")
    public RequestInterceptor provideRequestInterceptor(final SharedUserPrefs sharedUserPrefs) {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("Authorization", "Bearer " + sharedUserPrefs.getAccessToken());
            }
        };
    }

    @Provides
    @ApplicationScope
    @Named("auth")
    public RequestInterceptor provideAuthRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                String key = AppConstants.CLIENT_ID + ":" + AppConstants.CLIENT_SECRET;
                request.addHeader("Authorization",
                        "Basic " + Arrays.toString(Base64.encode(key.getBytes(), Base64.DEFAULT)));
            }
        };
    }
}
