package com.jerbotron_mac.spotishake.network;

import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.network.responses.AuthResponse;

import javax.inject.Named;

import dagger.Provides;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface SpotifyAuthService {


    @POST("/api/token")
    @FormUrlEncoded
    void getAuthTokens(@Field("grant_type") String grantType,
                       @Field("code") String code,
                       @Field("redirect_uri") String redirectUri,
                       @Field("client_id") String clientId,
                       @Field("client_secret") String clientSecret,
                       Callback<AuthResponse> callback);

    @dagger.Module
    class Module {
        @Provides
        @ApplicationScope
        public SpotifyAuthService provideSpotifyAuthService(@Named("spotify-auth") RestAdapter restAdapter) {
            return restAdapter.create(SpotifyAuthService.class);
        }
    }
}
