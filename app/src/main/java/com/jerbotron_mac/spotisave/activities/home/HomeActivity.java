package com.jerbotron_mac.spotisave.activities.home;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnLookupLocalStream;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnStorageSqlite;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.jerbotron_mac.spotisave.R;
import com.jerbotron_mac.spotisave.activities.home.displayer.HomeDisplayer;
import com.jerbotron_mac.spotisave.data.DatabaseAdapter;
import com.jerbotron_mac.spotisave.gracenote.SystemEvents;
import com.jerbotron_mac.spotisave.utils.DeveloperUtils;

import static com.jerbotron_mac.spotisave.shared.AppConstants.APP_STRING;
import static com.jerbotron_mac.spotisave.shared.AppConstants.GNSDK_CLIENT_ID;
import static com.jerbotron_mac.spotisave.shared.AppConstants.GNSDK_CLIENT_TAG;
import static com.jerbotron_mac.spotisave.shared.AppConstants.GNSDK_LICENSE_FILENAME;


public class HomeActivity extends AppCompatActivity {

    // Gracenot SDK Objects
    private GnManager gnManager;
    private GnUser gnUser;

    private HomePresenter presenter;

    private static final int AUDIO_MICROPHONE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (!DeveloperUtils.hasMicrophonePermission(this)) {
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
        presenter = new HomePresenter(gnManager, gnUser, displayer, new DatabaseAdapter(this));
        displayer.setPresenter(presenter);
        presenter.start();
    }

    private void initGnSDK() {
        String gnsdkLicense = DeveloperUtils.getAssetAsString(GNSDK_LICENSE_FILENAME, getApplicationContext());
        if (gnsdkLicense == null) {
            DeveloperUtils.showToast(this, "Error initializing GnSDK");
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
}
