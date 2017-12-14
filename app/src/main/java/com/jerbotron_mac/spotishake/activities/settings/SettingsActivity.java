package com.jerbotron_mac.spotishake.activities.settings;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.settings.dagger.DaggerSettingsComponent;
import com.jerbotron_mac.spotishake.activities.settings.dagger.SettingsComponent;
import com.jerbotron_mac.spotishake.application.SpotiShakeApplication;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

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

    private static MainPreferencesFragment mainPreferencesFragment;

    private static final int REQUEST_CODE = 1337;

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
    private static void bindPreferenceSummaryToValue(Preference preference) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        SettingsComponent settingsComponent = DaggerSettingsComponent.builder()
                .applicationComponent(((SpotiShakeApplication) getApplication()).getApplicationComponent())
                .build();

        presenter = new SettingsPresenter(settingsComponent);

        mainPreferencesFragment = new MainPreferencesFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mainPreferencesFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
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
    }

    private void onAuthenticationComplete(AuthenticationResponse authResponse) {
        // Once we have obtained an authorization token, we can proceed with creating a Player.
        Log.d("JWDEBUG", "Got authentication token");
//        if (mPlayer == null) {
//            Config playerConfig = new Config(getApplicationContext(), authResponse.getAccessToken(), CLIENT_ID);
//            // Since the Player is a static singleton owned by the Spotify class, we pass "this" as
//            // the second argument in order to refcount it properly. Note that the method
//            // Spotify.destroyPlayer() also takes an Object argument, which must be the same as the
//            // one passed in here. If you pass different instances to Spotify.getPlayer() and
//            // Spotify.destroyPlayer(), that will definitely result in resource leaks.
//            mPlayer = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
//                @Override
//                public void onInitialized(SpotifyPlayer player) {
//                    logStatus("-- Player initialized --");
//                    mPlayer.setConnectivityStatus(mOperationCallback, getNetworkConnectivity(DemoActivity.this));
//                    mPlayer.addNotificationCallback(DemoActivity.this);
//                    mPlayer.addConnectionStateCallback(DemoActivity.this);
//                    // Trigger UI refresh
//                    updateView();
//                }
//
//                @Override
//                public void onError(Throwable error) {
//                    logStatus("Error in initialization: " + error.getMessage());
//                }
//            });
//        } else {
//            mPlayer.login(authResponse.getAccessToken());
//        }
    }

        @Override
    public void onBackPressed() {
        super.onBackPressed();
        presenter.stop();
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

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || MainPreferencesFragment.class.getName().equals(fragmentName);
    }

    private void deleteHistory() {
        presenter.deleteHistory();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MainPreferencesFragment extends PreferenceFragment {

        private static final String CLIENT_ID = "ec58f1c44e1d4a2ca919bde5bb7bbc13";
        private static final String REDIRECT_URI = "spotishake://callback";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
//            bindPreferenceSummaryToValue(findPreference("account_login"));
//            bindPreferenceSummaryToValue(findPreference("auto_save_songs"));
//            bindPreferenceSummaryToValue(findPreference("delete_history"));

            Preference loginPref = findPreference("account_login");
            SwitchPreference autoSavePref = (SwitchPreference) findPreference("auto_save_songs");
            final Preference deleteHistoryPref = findPreference("delete_history");

            loginPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    openLoginWindow();
                    return true;
                }
            });

            autoSavePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    return false;
                }
            });

            deleteHistoryPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    onDeleteHistoryClick();
                    return true;
                }
            });
        }

        private void openLoginWindow() {
            final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                    .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"})
                    .build();

            AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
        }

        public void onDeleteHistoryClick() {
            final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            View view = alertDialog.getLayoutInflater().inflate(R.layout.dialog_delete_history, null);

            Button cancel = (Button) view.findViewById(R.id.cancel_delete);
            Button delete = (Button) view.findViewById(R.id.confirm_delete);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SettingsActivity) getActivity()).deleteHistory();
                    alertDialog.dismiss();
                }
            });

            alertDialog.setView(view);
            alertDialog.show();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
