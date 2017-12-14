package com.jerbotron_mac.spotishake.gracenote;

import android.support.annotation.IntDef;
import android.util.Log;

import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnMusicIdStreamIdentifyingStatus;
import com.gracenote.gnsdk.GnMusicIdStreamProcessingStatus;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;
import com.jerbotron_mac.spotishake.activities.home.HomePresenter;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

public class MusicIdStreamEvents implements IGnMusicIdStreamEvents {

    private HomePresenter presenter;

    private final static int NUM_OF_RETRIES = 2;
    private int retryCount = 0;

    private PublishSubject<Integer> retrySubject;

    public MusicIdStreamEvents(HomePresenter presenter) {
        this.presenter = presenter;
        retrySubject = PublishSubject.create();
        retrySubject.observeOn(AndroidSchedulers.mainThread())
                .subscribe(presenter.getRetrySubscriber());
    }

    @Override
    public void statusEvent(GnStatus status, long percentComplete, long bytesTotalSent, long bytesTotalReceived, IGnCancellable cancellable ) {
    }

    @Override
    public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus status,
                                                   IGnCancellable canceller) {
        switch (status) {
            case kStatusProcessingAudioStarted: {
                break;
            }
        }
    }

    @Override
    public void musicIdStreamIdentifyingStatusEvent(GnMusicIdStreamIdentifyingStatus status,
                                                    IGnCancellable canceller ) {
        switch (status) {
            case kStatusIdentifyingStarted: {
                break;
            }
            case kStatusIdentifyingEnded: {
                break;
            }
        }
    }


    @Override
    public void musicIdStreamAlbumResult(GnResponseAlbums result, IGnCancellable canceller ) {
        if (result.resultCount() > 0) {
            retrySubject.onNext(IdentifyState.IDENTIFIED);
            presenter.updateAlbum(result);
            retryCount = 0;
        } else {
//            if (retryCount < NUM_OF_RETRIES) {
//                retryIdentify();
//            } else {
//                resetRetryCount();
//            }
            retrySubject.onNext(IdentifyState.NOT_FOUND);
        }
    }

    @Override
    public void musicIdStreamIdentifyCompletedWithError(GnError error) {
        Log.e(getClass().getName(), "Identify error: " + error.errorDescription());
    }

    private void retryIdentify() {
        retryCount++;
        retrySubject.onNext(IdentifyState.RETRY);
        presenter.tryIdentifyMusic();
    }

    private void resetRetryCount() {
        retryCount = 0;
        retrySubject.onNext(IdentifyState.NOT_FOUND);
    }

    @IntDef(value = {})
    public @interface IdentifyState {
        int UNKNOWN = 0;
        int IDENTIFIED  = 1;
        int RETRY = 2;
        int NOT_FOUND = 3;
    }
}
