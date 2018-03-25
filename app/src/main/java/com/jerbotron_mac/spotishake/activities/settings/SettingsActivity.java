package com.jerbotron_mac.spotishake.activities.settings;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;


import com.jerbotron_mac.spotishake.activities.settings.dagger.DaggerSettingsComponent;
import com.jerbotron_mac.spotishake.activities.settings.dagger.SettingsComponent;
import com.jerbotron_mac.spotishake.activities.settings.fragments.MainPreferencesFragment;
import com.jerbotron_mac.spotishake.application.SpotiShakeApplication;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import javax.inject.Inject;

import static com.jerbotron_mac.spotishake.shared.AppConstants.SETTINGS_PREF_REQUEST_CODE;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    private SettingsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        SettingsComponent settingsComponent = DaggerSettingsComponent.builder()
                .applicationComponent(((SpotiShakeApplication) getApplication()).getApplicationComponent())
                .build();

        settingsComponent.inject(this);

        MainPreferencesFragment mainPreferencesFragment = new MainPreferencesFragment();
        presenter = new SettingsPresenter(settingsComponent, mainPreferencesFragment);
        mainPreferencesFragment.init(presenter);

        getFragmentManager().beginTransaction().replace(android.R.id.content, mainPreferencesFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == SETTINGS_PREF_REQUEST_CODE) {
            presenter.handleSpotifyLogin(resultCode, intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
