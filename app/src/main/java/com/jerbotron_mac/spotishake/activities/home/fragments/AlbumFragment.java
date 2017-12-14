package com.jerbotron_mac.spotishake.activities.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gracenote.gnsdk.GnAlbum;
import com.gracenote.gnsdk.GnAlbumIterator;
import com.gracenote.gnsdk.GnDataLevel;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnImageSize;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.HomePresenter;
import com.jerbotron_mac.spotishake.activities.home.displayer.HomeDisplayer;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.squareup.picasso.Picasso;

public class AlbumFragment extends Fragment {

    private HomeDisplayer displayer;
    private Vibrator vibrator;

    private ImageView coverArt;
    private ScrollView songInfoContainer;
    private TextView songTitle;
    private TextView songAlbum;
    private TextView songArtist;
    private TextView songGenre;
    private TextView emptyText;

    private boolean hasHistory = false;
    private String coverArtUrl;
    private String track;
    private String album;
    private String artist;
    private String genre;

    public AlbumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        coverArt = (ImageView) view.findViewById(R.id.cover_art);
        songInfoContainer = (ScrollView) view.findViewById(R.id.album_info_container);
        songTitle = (TextView) view.findViewById(R.id.song_title);
        songAlbum = (TextView) view.findViewById(R.id.song_album);
        songArtist = (TextView) view.findViewById(R.id.song_artist);
        songGenre = (TextView) view.findViewById(R.id.song_genre);
        emptyText = (TextView) view.findViewById(R.id.empty_text);

        if (hasHistory) {
            updateAlbumUI(coverArtUrl, track, album, artist, genre);
        }

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

    private void toggleAlbumDisplay() {
        if (hasHistory) {
            emptyText.setVisibility(View.INVISIBLE);
            coverArt.setVisibility(View.VISIBLE);
            songInfoContainer.setVisibility(View.VISIBLE);
        } else {
            emptyText.setVisibility(View.VISIBLE);
            coverArt.setVisibility(View.INVISIBLE);
            songInfoContainer.setVisibility(View.INVISIBLE);
        }
    }


    public void updateAlbum(GnResponseAlbums results) {
        hasHistory = true;
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

    private void updateAlbumUI(String coverArtUrl,
                               String track,
                               String album,
                               String artist,
                               String genre) {
        toggleAlbumDisplay();
        if (coverArt != null && songTitle != null && songArtist != null && songGenre != null) {
            String url = AppUtils.prependHttp(coverArtUrl);
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

    private void cacheAlbumInfo(String coverArtUrl,
                                String track,
                                String album,
                                String artist,
                                String genre) {
        this.coverArtUrl = coverArtUrl;
        this.track = track;
        this.album = album;
        this.artist = artist;
        this.genre = genre;
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

                String trackArtist = gnAlbum.trackMatched().artist().name().display();
                if (trackArtist == null || trackArtist.isEmpty()) {
                    //use album artist if track artist not available
                    trackArtist = gnAlbum.artist().name().display();
                }

                cacheAlbumInfo(gnAlbum.coverArt().asset(GnImageSize.kImageSizeLarge).url(),
                        gnAlbum.trackMatched().title().display(),
                        gnAlbum.title().display(),
                        trackArtist,
                        gnAlbum.trackMatched().genre(GnDataLevel.kDataLevel_1));
                updateAlbumUI(coverArtUrl, track, album, artist, genre);
                vibrator.vibrate(750);
            }
        }
    }
}
