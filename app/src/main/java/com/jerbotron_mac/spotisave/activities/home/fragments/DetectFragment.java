package com.jerbotron_mac.spotisave.activities.home.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jerbotron_mac.spotisave.R;
import com.jerbotron_mac.spotisave.activities.home.HomePresenter;
import com.jerbotron_mac.spotisave.activities.home.custom.ShakeDetector;
import com.jerbotron_mac.spotisave.utils.DeveloperUtils;

public class DetectFragment extends Fragment {

    private View mainLogo;

    private static final float zeroScaleFactor = 0.50f;
    private static final float maxScaleFactor = 1.50f;
    private int currentPercent = 50;
    private volatile boolean isRunning = false;
    private volatile boolean isAudioProcessingStarted = false;

    private volatile Activity activity;

    private FrameLayout.LayoutParams layoutParams;
    private int logoImageHeight, logoImageWidth;

    private HomePresenter presenter;
    private ShakeDetector shakeDetector;
    private ShakeListener shakeListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detect, container, false);
        mainLogo = view.findViewById(R.id.main_logo);
        layoutParams = (FrameLayout.LayoutParams) mainLogo.getLayoutParams();

//        mainLogo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                presenter.tryIdentifyMusic();
//            }
//        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        BitmapDrawable bd =(BitmapDrawable) ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.spotify_logo);
        logoImageHeight = (int) ((float) bd.getBitmap().getHeight() * zeroScaleFactor);
        logoImageWidth = (int) ((float) bd.getBitmap().getWidth() * zeroScaleFactor);
        initShakeSensor(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shakeDetector.unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        isRunning = true;
        activity = getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("IS_RUNNING", isRunning);
    }

    private void initShakeSensor(Context context) {
        shakeDetector = new ShakeDetector((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        shakeListener = new ShakeListener();
        shakeDetector.registerShakeListener(shakeListener);
    }

    public void setPresenter(HomePresenter presenter) {
        this.presenter = presenter;
    }

    public void setIsAudioProcessingStarted(boolean isAudioProcessingStarted) {
        this.isAudioProcessingStarted = isAudioProcessingStarted;
    }

    public void setAmplitudePercent(int amplitudePercent) {
        if (isRunning && currentPercent != amplitudePercent) {
            scaleAmplitude(amplitudePercent);
            currentPercent = amplitudePercent;
        }
    }

    private void scaleAmplitude(int percent) {
        if (activity != null && !isAudioProcessingStarted) {
            SetDisplayAmplitudeRunnable setDisplayAmplitudeRunnable = new SetDisplayAmplitudeRunnable(percent);
            activity.runOnUiThread(setDisplayAmplitudeRunnable);
        }
    }

    private class SetDisplayAmplitudeRunnable implements Runnable {

        int percent;

        SetDisplayAmplitudeRunnable(int percent) {
            this.percent = percent;
        }

        @Override
        public void run() {
            float scaleFactor = zeroScaleFactor + ((float) percent/100); // zero position plus audio wave amplitude percent
            scaleFactor = (scaleFactor > maxScaleFactor) ? maxScaleFactor : scaleFactor;

            layoutParams.height = (int)((float) logoImageHeight * scaleFactor);
            layoutParams.width = (int)((float) logoImageWidth * scaleFactor);
            mainLogo.setLayoutParams(layoutParams);
        }
    }

    private class ShakeListener implements ShakeDetector.OnShakeListener {
        @Override
        public void onShake() {
            if (!isAudioProcessingStarted) {
                DeveloperUtils.showToast(getContext(), "Analyzing song...");
                presenter.tryIdentifyMusic();
                isAudioProcessingStarted = true;
            }
        }
    }
}
