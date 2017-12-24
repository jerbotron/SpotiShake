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

import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.HomePresenter;
import com.jerbotron_mac.spotishake.activities.home.HomeDisplayer;
import com.jerbotron_mac.spotishake.data.SongInfo;
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
    private SongInfo cachecSongInfo;

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
            updateAlbumUI();
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


    public void updateAlbum(SongInfo songInfo) {
        hasHistory = true;
        UpdateAlbumRunnable updateAlbumRunnable = new UpdateAlbumRunnable(songInfo);
        getActivity().runOnUiThread(updateAlbumRunnable);
    }

    private void updateAlbumUI() {
        toggleAlbumDisplay();
        if (cachecSongInfo != null) {
            String url = AppUtils.prependHttp(cachecSongInfo.getCoverArtUrl());
            Picasso.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.spotify_logo)
                    .into(coverArt);
            songTitle.setText(cachecSongInfo.getTitle());
            songAlbum.setText(cachecSongInfo.getAlbum());
            songArtist.setText(cachecSongInfo.getArtist());
            songGenre.setText(cachecSongInfo.getGenre());
        }
    }

    private void setCachecSongInfo(SongInfo songInfo) {
        this.cachecSongInfo = songInfo;
    }

    private class UpdateAlbumRunnable implements Runnable {

        private SongInfo songInfo;

        private UpdateAlbumRunnable(SongInfo songInfo) {
            this.songInfo = songInfo;
        }

        @Override
        public void run() {
            displayer.setCurrentItem(HomePresenter.FragmentEnum.ALBUM);

            setCachecSongInfo(songInfo);
            updateAlbumUI();

            vibrator.vibrate(750);
        }
    }
}
