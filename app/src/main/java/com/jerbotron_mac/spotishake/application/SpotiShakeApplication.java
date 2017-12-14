package com.jerbotron_mac.spotishake.application;

import android.app.Application;

import com.jerbotron_mac.spotishake.dagger.ApplicationComponent;
import com.jerbotron_mac.spotishake.dagger.DaggerApplicationComponent;

public class SpotiShakeApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = getApplicationComponent();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getApplicationComponent() {
        if (applicationComponent == null) {
            return DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationComponent.ApplicationModule(this))
                    .build();
        }
        return applicationComponent;
    }

}
