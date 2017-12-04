package com.jerbotron_mac.spotisave.activities.settings;

import android.content.Context;

import com.jerbotron_mac.spotisave.data.DatabaseAdapter;

public class SettingsPresenter {

    private DatabaseAdapter databaseAdapter;

    public SettingsPresenter(Context context) {
        databaseAdapter = new DatabaseAdapter(context);
    }

    public void deleteHistory() {
        databaseAdapter.deleteAll();
    }

    public void stop() {
        databaseAdapter.close();
    }


}
