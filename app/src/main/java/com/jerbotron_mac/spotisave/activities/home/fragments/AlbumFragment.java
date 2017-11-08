package com.jerbotron_mac.spotisave.activities.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnDataLevel;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.jerbotron_mac.spotisave.R;
import com.jerbotron_mac.spotisave.activities.home.HomePresenter;
import com.jerbotron_mac.spotisave.activities.home.displayer.HomeDisplayer;
import com.jerbotron_mac.spotisave.utils.DeveloperUtils;
import com.squareup.picasso.Picasso;

public class AlbumFragment extends Fragment {

    private HomeDisplayer displayer;
    private Vibrator vibrator;

    private ImageView coverArt;
    private TextView songTitle;
    private TextView songAlbum;
    private TextView songArtist;
    private TextView songGenre;

    private boolean hasHistory = false;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        coverArt = (ImageView) view.findViewById(R.id.cover_art);
        songTitle = (TextView) view.findViewById(R.id.song_title);
        songAlbum = (TextView) view.findViewById(R.id.song_album);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songGenre = (TextView) view.findViewById(R.id.song_genre);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void setDisplayer(HomeDisplayer displayer) {
        this.displayer = displayer;
    }

    public void updateAlbum(GnResponseAlbums results) {
        if (results.resultCount() == 0) {
            Log.d(getClass().getName(), "empty results");
        } else {
            try {
                GnAlbumIterator iter = results.albums().getIterator();
                GnAlbum gnAlbum = null;
                while (iter.hasNext()) {
                    gnAlbum = iter.next();
                }

                UpdateAlbumRunnable updateAlbumRunnable = new UpdateAlbumRunnable(gnAlbum);
                getActivity().runOnUiThread(updateAlbumRunnable);

            } catch (GnException e) {
                e.printStackTrace();
            }
        }
        hasHistory = true;
    }

    private void updateAlbumUI(String coverArtUrl,
                               String track,
                               String album,
                               String artist,
                               String genre) {
        if (coverArt != null && songTitle != null && songArtist != null && songGenre != null) {
            String url = DeveloperUtils.prependHttp(coverArtUrl);
            Picasso.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.spotify_logo)
                    .into(coverArt);
            songTitle.setText(track);
            songAlbum.setText(album);
            songArtist.setText(artist);
            songGenre.setText(genre);
        }
    }

    private class UpdateAlbumRunnable implements Runnable {

        private GnAlbum gnAlbum;

        private UpdateAlbumRunnable(GnAlbum gnAlbum) {
            this.gnAlbum = gnAlbum;
        }

        @Override
        public void run() {
            if (gnAlbum != null) {
                displayer.setCurrentItem(HomePresenter.FragmentEnum.ALBUM);
                updateAlbumUI(gnAlbum.coverArt().asset(GnImageSize.kImageSizeLarge).url(),
                        gnAlbum.trackMatched().title().display(),
                        gnAlbum.title().display(),
                        gnAlbum.artist().name().display(),
                        gnAlbum.trackMatched().genre(GnDataLevel.kDataLevel_1));
                vibrator.vibrate(750);
            }
        }
    }
}
