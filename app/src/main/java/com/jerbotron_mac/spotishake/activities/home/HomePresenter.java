package com.jerbotron_mac.spotishake.activities.home;

import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnDescriptor;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnLanguage;
import com.gracenote.gnsdk.GnLocale;
import com.gracenote.gnsdk.GnLocaleGroup;
import com.gracenote.gnsdk.GnLookupData;
import com.gracenote.gnsdk.GnMic;
import com.gracenote.gnsdk.GnMusicIdStream;
import com.gracenote.gnsdk.GnMusicIdStreamPreset;
import com.gracenote.gnsdk.GnRegion;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.IGnAudioSource;
import com.jerbotron_mac.spotishake.activities.home.adapters.AudioVisualizerAdapter;
import com.jerbotron_mac.spotishake.activities.home.dagger.HomeComponent;
import com.jerbotron_mac.spotishake.activities.home.fragments.AlbumFragment;
import com.jerbotron_mac.spotishake.activities.home.fragments.DetectFragment;
import com.jerbotron_mac.spotishake.activities.home.fragments.HistoryFragment;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;
import com.jerbotron_mac.spotishake.gracenote.MusicIdStreamEvents;
import com.jerbotron_mac.spotishake.network.SpotifyServiceWrapper;
import com.jerbotron_mac.spotishake.network.TrackData;
import com.jerbotron_mac.spotishake.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.Observable;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

import static com.jerbotron_mac.spotishake.shared.AppConstants.APP_STRING;

public class HomePresenter {

    @Inject DatabaseAdapter databaseAdapter;
    @Inject AppUtils appUtils;
    @Inject SpotifyServiceWrapper spotifyServiceWrapper;

    private HomeDisplayer displayer;
    private AlbumFragment albumFragment;
    private DetectFragment detectFragment;
    private HistoryFragment historyFragment;

    // Gracenot SDK Objects
    private GnUser gnUser;
    private GnMusicIdStream gnMusicIdStream;
    private IGnAudioSource gnAudioSource;
    private List<GnMusicIdStream> musicIdStreams = new ArrayList<>();

    private @FragmentEnum int currentFragment;

    HomePresenter(GnUser gnUser, HomeDisplayer displayer, HomeComponent homeComponent) {
        this.gnUser = gnUser;
        this.displayer = displayer;
        homeComponent.inject(this);
    }

    void start() {
        try {
            initGnLocale();
            gnAudioSource = new AudioVisualizerAdapter(new GnMic(), this);
            gnMusicIdStream = new GnMusicIdStream(gnUser,
                                                  GnMusicIdStreamPreset.kPresetMicrophone,
                                                  new MusicIdStreamEvents(this));
            gnMusicIdStream.options().lookupData(GnLookupData.kLookupDataContent, true);
            gnMusicIdStream.options().lookupData(GnLookupData.kLookupDataSonicData, true);
            gnMusicIdStream.options().resultSingle(true);
            musicIdStreams.add(gnMusicIdStream);
        } catch (GnException e) {
            e.printStackTrace();
        }

        initFragments();
        displayer.start(albumFragment, detectFragment, historyFragment);
    }

    void resume() {
        if (gnMusicIdStream != null) {
            // Create a thread to process the data pulled from GnMic
            // Internally pulling data is a blocking call, repeatedly called until
            // audio processing is stopped. This cannot be called on the main thread.
            Thread audioProcessThread = new Thread(new AudioProcessRunnable());
            audioProcessThread.start();
        }

        if (currentFragment == FragmentEnum.HISTORY) {
            historyFragment.refreshView();
        }

        databaseAdapter.open();
    }

    void pause() {
        if ( gnMusicIdStream != null ) {
            try {
                // to ensure no pending identifications deliver results while your app is
                // paused it is good practice to call cancel
                // it is safe to call identifyCancel if no identify is pending
                gnMusicIdStream.identifyCancel();

                // stopping audio processing stops the audio processing thread started
                // in onResume
                gnMusicIdStream.audioProcessStop();
            } catch (GnException e) {
                Log.e(APP_STRING, e.errorCode() + ", " + e.errorDescription() + ", " + e.errorModule());
                e.printStackTrace();
            }
        }
    }

    public void setCurrentFragment(int position) {
        this.currentFragment = position;
    }

    private void initFragments() {
        albumFragment = new AlbumFragment();
        albumFragment.setDisplayer(displayer);
        detectFragment = new DetectFragment();
        detectFragment.setPresenter(this);
        historyFragment = new HistoryFragment();
        historyFragment.setDatabaseAdapter(databaseAdapter);
    }

    private void initGnLocale() {
        Observable.just(gnUser)
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableObserver<GnUser>() {
                    @Override
                    public void onNext(GnUser gnUser) {
                        try {
                            GnLocale locale = new GnLocale(GnLocaleGroup.kLocaleGroupMusic,
                                    GnLanguage.kLanguageEnglish,
                                    GnRegion.kRegionGlobal,
                                    GnDescriptor.kDescriptorDefault,
                                    gnUser);
                            locale.setGroupDefault();
                        } catch (GnException e) {
                            Log.e(getClass().getName(), e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void setAmplitudePercent(int amplitudePercent) {
        if (displayer.getCurrentItem() == FragmentEnum.DETECT) {
            detectFragment.setAmplitudePercent(amplitudePercent);
        }
    }

    public void tryIdentifyMusic() {
        try {
            gnMusicIdStream.identifyAlbumAsync();
        } catch (GnException e) {
            e.printStackTrace();
        }
    }

    public void updateAlbum(GnResponseAlbums responseAlbums) {
        addSongToSpotify(responseAlbums);
        albumFragment.updateAlbum(responseAlbums);
        historyFragment.saveSong(responseAlbums);
    }

    public void addSongToSpotify(GnResponseAlbums gnAlbums) {
        GnAlbumIterator iter = gnAlbums.albums().getIterator();
        try {
            TrackData.Builder builder = new TrackData.Builder();
            while (iter.hasNext()) {
                GnAlbum album = iter.next();
                if (album.title().display() != null) {
                    builder.setAlbum(album.title().display());
                }

                if (album.trackMatched() != null) {
                    String trackArtist = album.trackMatched().artist().name().display();
                    if (trackArtist == null || trackArtist.isEmpty()) {
                        //use album artist if track artist not available
                        trackArtist = album.artist().name().display();
                    }
                    builder.setArtist(trackArtist);
                    builder.setTrack(album.trackMatched().title().display());
                }
            }

            spotifyServiceWrapper.saveTrackToSavedSongs(builder.build());
        } catch (GnException e) {
            e.printStackTrace();
        }
    }

    public Consumer<Integer> getRetrySubscriber() {
        return new Consumer<Integer>() {
            @Override
            public void accept(Integer s) throws Exception {
                switch (s) {
                    case MusicIdStreamEvents.IdentifyState.IDENTIFIED: {
                        detectFragment.setLoadingIndicator(false);
                        break;
                    }
                    case MusicIdStreamEvents.IdentifyState.RETRY: {
                        appUtils.showToast("Still identifying...", Toast.LENGTH_SHORT);
                        break;
                    }
                    case MusicIdStreamEvents.IdentifyState.NOT_FOUND: {
                        appUtils.showToast("Could not ID song, please try again.", Toast.LENGTH_SHORT);
                        detectFragment.handleSongNotFound();
                        break;
                    }
                }
            }
        };
    }

    /**
     * GnMusicIdStream object processes audio read directly from GnMic object
     */
    private class AudioProcessRunnable implements Runnable {
        @Override
        public void run() {
            try {
                // resume audio processing with GnMic, GnMusicIdStream pulls data from GnMic internally
                gnMusicIdStream.audioProcessStart(gnAudioSource);
            } catch (GnException e) {
                Log.e(APP_STRING, e.errorCode() + ", " + e.errorDescription() + ", " + e.errorModule());
                e.printStackTrace();
            }
        }
    }

    @IntDef({FragmentEnum.ALBUM, FragmentEnum.DETECT, FragmentEnum.HISTORY})
    public @interface FragmentEnum {
        int ALBUM = 0;
        int DETECT = 1;
        int HISTORY = 2;
    }
}
