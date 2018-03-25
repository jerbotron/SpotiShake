package com.jerbotron_mac.spotishake.activities.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLookupLocalStream;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnStorageSqlite;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.dagger.HomeComponent;
import com.jerbotron_mac.spotishake.activities.settings.SettingsActivity;
import com.jerbotron_mac.spotishake.application.SpotiShakeApplication;
import com.jerbotron_mac.spotishake.gracenote.SystemEvents;
import com.jerbotron_mac.spotishake.network.SpotifyAuthService;
import com.jerbotron_mac.spotishake.network.SpotifyServiceWrapper;
import com.jerbotron_mac.spotishake.network.callbacks.LoginCallback;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import javax.inject.Inject;

import static com.jerbotron_mac.spotishake.shared.AppConstants.APP_STRING;
import static com.jerbotron_mac.spotishake.shared.AppConstants.GNSDK_CLIENT_ID;
import static com.jerbotron_mac.spotishake.shared.AppConstants.GNSDK_CLIENT_TAG;
import static com.jerbotron_mac.spotishake.shared.AppConstants.GNSDK_LICENSE_FILENAME;
import static com.jerbotron_mac.spotishake.shared.AppConstants.SETTINGS_PREF_REQUEST_CODE;


public class HomeActivity extends AppCompatActivity {

    @Inject AppUtils appUtils;
    @Inject SpotifyAuthService authService;
    @Inject SharedUserPrefs sharedUserPrefs;
    @Inject SpotifyServiceWrapper spotifyServiceWrapper;

    // Gracenot SDK Objects
    private GnManager gnManager;
    private GnUser gnUser;

    private HomeComponent homeComponent;
    private HomePresenter presenter;

    private static final int AUDIO_MICROPHONE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeComponent = ((SpotiShakeApplication) getApplication()).getHomeComponent();
        homeComponent.inject(this);

        if (sharedUserPrefs.isUserLoggedIn()) {
            // make sure access token is valid
            spotifyServiceWrapper.getUser(new LoginCallback(this, sharedUserPrefs));
        }

        if (!appUtils.hasMicrophonePermission(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_MICROPHONE_REQUEST_CODE);
        } else {

            if (savedInstanceState != null) {
                String t = savedInstanceState.getString("test");
                if (t != null)
                    Log.d("JWDEBUG", t);
            }
            startup();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("test", "testing");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AUDIO_MICROPHONE_REQUEST_CODE: {
                startup();
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == SETTINGS_PREF_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            sharedUserPrefs.setAccessToken(response.getAccessToken());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.pause();
        }
    }

    private void startup() {
        initGnSDK();
        HomeDisplayer displayer = new HomeDisplayer(this);
        presenter = new HomePresenter(gnUser, displayer, homeComponent, getSupportFragmentManager());
        displayer.setPresenter(presenter);
        presenter.start();

        if (!isNetworkConnected()) {
            AppUtils.showCheckNetworkConnectionDialog(this);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void initGnSDK() {
        String gnsdkLicense = appUtils.getAssetAsString(GNSDK_LICENSE_FILENAME);
        if (gnsdkLicense == null) {
            appUtils.showToast("Error initializing GnSDK", Toast.LENGTH_SHORT);
            return;
        }

        try {
            // gnManager must be created first to initialize the GnSDK
            gnManager = new GnManager(getApplicationContext(), gnsdkLicense, GnLicenseInputMode.kLicenseInputModeString);
            gnUser = new GnUser(new GnUserStore(getApplicationContext()), GNSDK_CLIENT_ID, GNSDK_CLIENT_TAG, APP_STRING);
            gnManager.systemEventHandler(new SystemEvents(gnUser));

            // enable storage provider allowing GNSDK to use its persistent stores
            GnStorageSqlite.enable();

            // enable local MusicID-Stream recognition (GNSDK storage provider must be enabled as pre-requisite)
            GnLookupLocalStream.enable();
        } catch (GnException e) {
            e.printStackTrace();
        }
    }

    public void launchSettingsActivity() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public HomeComponent getHomeComponent() {
        if (homeComponent == null) {
            return ((SpotiShakeApplication) getApplication()).getHomeComponent();
        }
        return homeComponent;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
