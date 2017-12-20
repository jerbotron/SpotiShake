package com.jerbotron_mac.spotishake.network;

import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.network.response.AuthResponse;

import dagger.Provides;
import io.reactivex.Observable;
import retrofit.http.Body;
import retrofit.http.POST;

public interface SpotifyAuthService {


    @POST("/api/token")
    Observable<AuthResponse> getAuthTokens(@Body AuthRequest request);

    @dagger.Module
    class Module {

        @Provides
        @ApplicationScope
        public SpotifyAuthService provideSpotifyAuthService() {

        }
    }
}
