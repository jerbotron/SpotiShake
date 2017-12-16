package com.jerbotron_mac.spotishake.activities.settings;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;

import com.jerbotron_mac.spotishake.activities.settings.dagger.SettingsComponent;
import com.jerbotron_mac.spotishake.activities.settings.fragments.MainPreferencesFragment;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;
import com.jerbotron_mac.spotishake.network.SpotifyServiceWrapper;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import javax.inject.Inject;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class SettingsPresenter {

    @Inject DatabaseAdapter databaseAdapter;
    @Inject SharedUserPrefs sharedUserPrefs;
    @Inject SpotifyServiceWrapper spotifyServiceWrapper;

    private MainPreferencesFragment mainPreferencesFragment;

    public SettingsPresenter(SettingsComponent settingsComponent,
                             MainPreferencesFragment mainPreferencesFragment) {
        settingsComponent.inject(this);
        this.mainPreferencesFragment = mainPreferencesFragment;
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    public static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }
    }

    public void handleSpotifyLogin(int resultCode, Intent intent) {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
        switch (response.getType()) {
            // Response was successful and contains auth token
            case TOKEN:
                onAuthenticationComplete(response);
                break;

            // Auth flow returned an error
            case ERROR:
                Log.d("JWDEBUG", "Auth error: " + response.getError());
                break;

            // Most likely auth flow was cancelled
            default:
                Log.d("JWDEBUG", "Auth result: " + response.getType());
        }
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        sharedUserPrefs.setSpotifyAuthToken(authResponse.getAccessToken());
        spotifyServiceWrapper.setAccessToken(authResponse.getAccessToken());
        spotifyServiceWrapper.getUserProfile(new SpotifyCallback<UserPrivate>() {
            @Override
            public void failure(SpotifyError spotifyError) {

            }

            @Override
            public void success(UserPrivate userPrivate, Response response) {
                sharedUserPrefs.saveUserInfo(userPrivate);
                mainPreferencesFragment.handleUserLogin();
            }
        });
    }

    public void deleteHistory() {
        databaseAdapter.deleteAll();
    }

    public boolean isLoggedIn() {
        String spotifyAuthToken = sharedUserPrefs.getSpotifyAuthToken();
        return spotifyAuthToken != null && !spotifyAuthToken.equals("");
    }

    public void logoutSpotify() {
        sharedUserPrefs.clearUserData();
    }

    public SharedUserPrefs getSharedUserPrefs() {
        return sharedUserPrefs;
    }
}