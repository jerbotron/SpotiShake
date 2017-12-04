package com.jerbotron_mac.spotisave.activities.home.fragments;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.jerbotron_mac.spotisave.R;
import com.jerbotron_mac.spotisave.activities.home.HomePresenter;
import com.jerbotron_mac.spotisave.activities.home.custom.ShakeDetector;
import com.jerbotron_mac.spotisave.utils.DeveloperUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

public class DetectFragment extends Fragment {

    private View mainLogo;
    private View loadingIndicator;

    private static final float zeroScaleFactor = 1.0f;
    private static final float maxScaleFactor = 3.0f;
    private volatile float lastScaleFactor = zeroScaleFactor;
    private int currentPercent = 100;
    private volatile boolean isRunning = false;
    private volatile boolean isAudioProcessingStarted = false;

    private volatile Activity activity;

    private HomePresenter presenter;
    private ShakeDetector shakeDetector;

    private PublishSubject<Integer> amplitudeSubject;
    private Consumer<Integer> amplitudeSubscriber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amplitudeSubject = PublishSubject.create();
        amplitudeSubscriber = new SetDisplayAmplitudeSubscriber();
        amplitudeSubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(amplitudeSubscriber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detect, container, false);
        mainLogo = view.findViewById(R.id.main_logo);
        loadingIndicator = view.findViewById(R.id.loading_indicator);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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

    private void initShakeSensor(Context context) {
        shakeDetector = new ShakeDetector((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        shakeDetector.registerShakeListener(new ShakeListener());
    }

    public void setPresenter(HomePresenter presenter) {
        this.presenter = presenter;
    }

    public void handleSongNotFound() {
        setLoadingIndicator(false);
        setIsAudioProcessingStarted(false);
    }

    public void setIsAudioProcessingStarted(boolean isAudioProcessingStarted) {
        this.isAudioProcessingStarted = isAudioProcessingStarted;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setAmplitudePercent(int amplitudePercent) {
        if (isRunning && currentPercent != amplitudePercent) {
            scaleAmplitude(amplitudePercent);
            currentPercent = amplitudePercent;
        }
    }

    public void setLoadingIndicator(boolean isVisible) {
        if (isVisible) {
            loadingIndicator.setVisibility(View.VISIBLE);
        } else {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private void scaleAmplitude(int percent) {
        if (activity != null) {
            amplitudeSubject.onNext(percent);
        }
    }

    private class SetDisplayAmplitudeSubscriber implements Consumer<Integer> {
        @Override
        public void accept(Integer percent) throws Exception {
            float scaleFactor = zeroScaleFactor + maxScaleFactor*((float) percent/100); // zero position plus audio wave amplitude percent

//            Log.d(DetectFragment.class.getName(), "percent = " + percent);
//            Log.d(DetectFragment.class.getName(), "scaleFactor = " + scaleFactor);

            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    lastScaleFactor, scaleFactor,
                    lastScaleFactor, scaleFactor,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            lastScaleFactor = scaleFactor;
            scaleAnimation.setInterpolator(new LinearInterpolator());
            scaleAnimation.setDuration(30);
            scaleAnimation.setFillEnabled(true);
            scaleAnimation.setFillAfter(true);

            mainLogo.startAnimation(scaleAnimation);
        }
    }

    private class ShakeListener implements ShakeDetector.OnShakeListener {
        @Override
        public void onShake() {
            if (isRunning && !isAudioProcessingStarted) {
                setLoadingIndicator(true);
                presenter.tryIdentifyMusic();
                isAudioProcessingStarted = true;
            }
        }
    }
}
