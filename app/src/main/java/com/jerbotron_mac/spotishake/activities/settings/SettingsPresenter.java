package com.jerbotron_mac.spotishake.activities.settings;

import com.jerbotron_mac.spotishake.activities.settings.dagger.SettingsComponent;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;

import javax.inject.Inject;

public class SettingsPresenter {

    @Inject DatabaseAdapter databaseAdapter;

    public SettingsPresenter(SettingsComponent settingsComponent) {
        settingsComponent.inject(this);
    }

    public void deleteHistory() {
        databaseAdapter.deleteAll();
    }

    public void stop() {
        databaseAdapter.close();
    }


}
