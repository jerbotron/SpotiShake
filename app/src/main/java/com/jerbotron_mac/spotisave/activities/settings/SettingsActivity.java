package com.jerbotron_mac.spotisave.activities.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jerbotron_mac.spotisave.R;

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

        presenter = new SettingsPresenter(this);

        mainPreferencesFragment = new MainPreferencesFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mainPreferencesFragment).commit();
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
                    return false;
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
                    return false;
                }
            });
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
