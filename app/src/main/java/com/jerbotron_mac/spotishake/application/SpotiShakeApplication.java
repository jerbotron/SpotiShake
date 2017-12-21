package com.jerbotron_mac.spotishake.application;

import android.app.Application;

import com.jerbotron_mac.spotishake.dagger.ApplicationComponent;
import com.jerbotron_mac.spotishake.dagger.DaggerApplicationComponent;
import com.jerbotron_mac.spotishake.network.RestAdapterModule;
import com.jerbotron_mac.spotishake.network.SpotifyAuthService;
import com.jerbotron_mac.spotishake.network.requests.AuthRequest;
import com.jerbotron_mac.spotishake.network.responses.AuthResponse;
import com.jerbotron_mac.spotishake.network.subscribers.AuthTokenSubscriber;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SpotiShakeApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Inject SharedUserPrefs sharedUserPrefs;
    @Inject SpotifyAuthService authService;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = getApplicationComponent();
        applicationComponent.inject(this);

        if (sharedUserPrefs.isUserLoggedIn()) {
//            authService.getAuthTokens(new AuthRequest(), new Callback<AuthResponse>() {
//                @Override
//                public void success(AuthResponse authResponse, Response response) {
//                    sharedUserPrefs.saveAuthData(authResponse);
//                }
//
//                @Override
//                public void failure(RetrofitError error) {
//                    error.printStackTrace();
//                }
//            });
        }
    }

    public ApplicationComponent getApplicationComponent() {
        if (applicationComponent == null) {
            return DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationComponent.ApplicationModule(this))
                    .restAdapterModule(new RestAdapterModule())
                    .build();
        }
        return applicationComponent;
    }

}
