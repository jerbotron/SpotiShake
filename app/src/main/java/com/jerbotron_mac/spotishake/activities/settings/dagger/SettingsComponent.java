package com.jerbotron_mac.spotishake.activities.settings.dagger;

import com.jerbotron_mac.spotishake.activities.settings.SettingsActivity;
import com.jerbotron_mac.spotishake.activities.settings.SettingsPresenter;
import com.jerbotron_mac.spotishake.dagger.ApplicationComponent;
import com.jerbotron_mac.spotishake.dagger.scopes.ActivityScope;

import dagger.Component;

@ActivityScope
@Component(dependencies = ApplicationComponent.class)
public interface SettingsComponent {
    void inject(SettingsActivity target);
    void inject(SettingsPresenter target);
}
