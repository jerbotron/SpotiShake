package com.jerbotron_mac.spotishake.application;

import android.app.Application;

import com.jerbotron_mac.spotishake.dagger.ApplicationComponent;
import com.jerbotron_mac.spotishake.dagger.DaggerApplicationComponent;
import com.jerbotron_mac.spotishake.network.RestAdapterModule;
import com.jerbotron_mac.spotishake.network.SpotifyAuthService;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import javax.inject.Inject;

public class SpotiShakeApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Inject SharedUserPrefs sharedUserPrefs;
    @Inject SpotifyAuthService authService;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = getApplicationComponent();
        applicationComponent.inject(this);

//        if (sharedUserPrefs.isUserLoggedIn()) {
//            // use authService here for refresh token/auth flow to get new tokens
//        }
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
