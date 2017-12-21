package com.jerbotron_mac.spotishake.network;

import com.jerbotron_mac.spotishake.dagger.scopes.ApplicationScope;
import com.jerbotron_mac.spotishake.network.request.AuthRequest;
import com.jerbotron_mac.spotishake.network.response.AuthResponse;

import javax.inject.Named;

import dagger.Provides;
import io.reactivex.Observable;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

public interface SpotifyAuthService {


    @POST("/api/token")
    Observable<AuthResponse> getAuthTokens(@Body AuthRequest request);

    @dagger.Module
    class Module {
        @Provides
        @ApplicationScope
        public SpotifyAuthService provideSpotifyAuthService(@Named("spotify-auth") RestAdapter restAdapter) {
            return restAdapter.create(SpotifyAuthService.class);
        }
    }
}
