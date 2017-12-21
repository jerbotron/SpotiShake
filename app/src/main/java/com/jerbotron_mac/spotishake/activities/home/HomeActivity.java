package com.jerbotron_mac.spotishake.activities.home;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLookupLocalStream;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnStorageSqlite;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.dagger.DaggerHomeComponent;
import com.jerbotron_mac.spotishake.activities.home.dagger.HomeComponent;
import com.jerbotron_mac.spotishake.activities.settings.SettingsActivity;
import com.jerbotron_mac.spotishake.application.SpotiShakeApplication;
import com.jerbotron_mac.spotishake.gracenote.SystemEvents;
import com.jerbotron_mac.spotishake.network.SpotifyAuthService;
import com.jerbotron_mac.spotishake.network.requests.AuthRequest;
import com.jerbotron_mac.spotishake.network.responses.AuthResponse;
import com.jerbotron_mac.spotishake.network.subscribers.AuthTokenSubscriber;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.jerbotron_mac.spotishake.utils.SharedUserPrefs;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.jerbotron_mac.spotishake.shared.AppConstants.APP_STRING;
import static com.jerbotron_mac.spotishake.shared.AppConstants.GNSDK_CLIENT_ID;
import static com.jerbotron_mac.spotishake.shared.AppConstants.GNSDK_CLIENT_TAG;
import static com.jerbotron_mac.spotishake.shared.AppConstants.GNSDK_LICENSE_FILENAME;


public class HomeActivity extends AppCompatActivity {

    @Inject AppUtils appUtils;
    @Inject SpotifyAuthService authService;
    @Inject SharedUserPrefs sharedUserPrefs;

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

        homeComponent = DaggerHomeComponent.builder()
                .applicationComponent(((SpotiShakeApplication) getApplication()).getApplicationComponent())
                .build();
        homeComponent.inject(this);

        if (!appUtils.hasMicrophonePermission(this)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_MICROPHONE_REQUEST_CODE);
        } else {
            startup();
        }
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
        presenter = new HomePresenter(gnUser, displayer, homeComponent);
        displayer.setPresenter(presenter);
        presenter.start();
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
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
