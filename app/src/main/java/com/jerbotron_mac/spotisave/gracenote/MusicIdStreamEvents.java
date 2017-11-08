package com.jerbotron_mac.spotisave.gracenote;

import android.util.Log;

import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnMusicIdStreamIdentifyingStatus;
import com.gracenote.gnsdk.GnMusicIdStreamProcessingStatus;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;
import com.jerbotron_mac.spotisave.activities.home.HomePresenter;

public class MusicIdStreamEvents implements IGnMusicIdStreamEvents {

    private HomePresenter presenter;

    public MusicIdStreamEvents(HomePresenter presenter) {
        this.presenter = presenter;

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
            case kStatusIdentifyingLocalQueryStarted: {
                break;
            }
            case kStatusIdentifyingOnlineQueryStarted: {
                break;
            }
            case kStatusIdentifyingEnded: {
                break;
            }
        }
    }


    @Override
    public void musicIdStreamAlbumResult(GnResponseAlbums result, IGnCancellable canceller ) {
        presenter.updateAlbum(result);
    }

    @Override
    public void musicIdStreamIdentifyCompletedWithError(GnError error) {
        Log.e(getClass().getName(), "Identify error: " + error.errorDescription());
    }
}
