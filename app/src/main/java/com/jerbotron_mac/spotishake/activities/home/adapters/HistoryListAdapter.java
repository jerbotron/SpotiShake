package com.jerbotron_mac.spotishake.activities.home.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.HomePresenter;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;
import com.jerbotron_mac.spotishake.data.SongInfo;
import com.jerbotron_mac.spotishake.network.subscribers.SpotifyUtils;
import com.jerbotron_mac.spotishake.shared.MaterialColor;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;

import static android.provider.BaseColumns._ID;
import static com.jerbotron_mac.spotishake.data.SongInfo.CARD_COLOR;
import static com.jerbotron_mac.spotishake.data.SongInfo.COVER_ART_URL;
import static com.jerbotron_mac.spotishake.data.SongInfo.SPOTIFY_ID;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_ALBUM;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_ARTIST;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_TITLE;

public class HistoryListAdapter extends CursorRecyclerAdapter<HistoryListAdapter.SongViewHolder> {

    private Context context;
    private HomePresenter presenter;
    private DatabaseAdapter databaseAdapter;

    public HistoryListAdapter(Context context,
                              HomePresenter presenter,
                              DatabaseAdapter databaseAdapter) {
        super(databaseAdapter.getCursor());
        this.presenter = presenter;
        this.context = context;
        this.databaseAdapter = databaseAdapter;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_info_card, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, Cursor cursor) {
        holder.bindCursor(cursor);
    }

    public void saveSongToDb(SongInfo songInfo) {
        Observable.just(songInfo).subscribe(new InsertRowSubscriber());
    }

    public void deleteSongFromDb(int position) {
        cursor.moveToPosition(position);
        Observable.just(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)))
                .subscribe(new DeleteRowSubscriber());
    }

    public void refreshCursor() {
        changeCursor(databaseAdapter.getCursor());
    }

    public void refreshAdapter() {
        notifyDataSetChanged();
    }

    private class InsertRowSubscriber extends DisposableObserver<SongInfo> {
        @Override
        public void onNext(SongInfo songInfo) {
            databaseAdapter.insertChanges(songInfo);
            refreshCursor();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
        }
    }

    private class DeleteRowSubscriber extends DisposableObserver<Integer> {

        @Override
        public void onNext(Integer rowId) {
            databaseAdapter.deleteRow(String.valueOf(rowId));
            refreshCursor();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
        }
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout cardForeground, cardBackground;
        private ImageView coverArt;
        private TextView title;
        private TextView album;
        private TextView artist;
        private ImageView songChecked;

        private volatile String spotifySongId;

        private SongViewHolder(View view) {
            super(view);
            cardForeground = (RelativeLayout) view.findViewById(R.id.card_foreground);
            cardBackground = (RelativeLayout) view.findViewById(R.id.card_background);
            coverArt = (ImageView) view.findViewById(R.id.song_card_cover_art);
            title = (TextView) view.findViewById(R.id.song_card_title);
            album = (TextView) view.findViewById(R.id.song_card_album);
            artist = (TextView) view.findViewById(R.id.song_card_artist);
            songChecked = (ImageView) view.findViewById(R.id.song_card_checked);
        }

        private void bindCursor(final Cursor cursor) {
            @MaterialColor.MaterialColors int colorId = cursor.getInt(cursor.getColumnIndexOrThrow(CARD_COLOR));
            cardForeground.setBackgroundColor(context.getResources().getColor(MaterialColor.getValue(colorId)));
            String url = AppUtils.prependHttp(cursor.getString(cursor.getColumnIndexOrThrow(COVER_ART_URL)));
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.spotify_logo)
                    .into(coverArt);
            if (MaterialColor.shouldUseBlackText(colorId)) {
                title.setTextColor(Color.BLACK);
                artist.setTextColor(Color.BLACK);
                album.setTextColor(Color.BLACK);
                songChecked.setImageResource(R.drawable.ic_song_added_black);
            } else {
                songChecked.setImageResource(R.drawable.ic_song_added_white);
            }
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_TITLE)));
            artist.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_ARTIST)));
            album.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_ALBUM)));

            spotifySongId = cursor.getString(cursor.getColumnIndexOrThrow(SPOTIFY_ID));

            if (presenter.isUserLoggedIn()) {
                if (AppUtils.isStringEmpty(spotifySongId)) {
                    final SongInfo songInfo =  new SongInfo(title.getText().toString(),
                                                            artist.getText().toString(),
                                                            album.getText().toString());
                    presenter.searchTrackInSpotify(songInfo, new SearchTrackCallback(songInfo));
                } else {
                    presenter.checkIfSongSavedInSpotify(spotifySongId, new SongCheckedSubscriber());
                }
            } else {
                songChecked.setVisibility(View.GONE);
            }

            cardBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSpotifyDeeplinkDialog(spotifySongId);
                }
            });

            cardBackground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    presenter.openSpotifyDeeplink(spotifySongId);
                    return true;
                }
            });
        }

        public RelativeLayout getCardForeground() {
            return cardForeground;
        }

        public RelativeLayout getCardBackground() {
            return cardBackground;
        }

        private void showSpotifyDeeplinkDialog(final String spotifySongId) {
            final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            View view = alertDialog.getLayoutInflater().inflate(R.layout.dialog_spotify_deeplink, null);

            Button cancel = (Button) view.findViewById(R.id.cancel_action);
            Button delete = (Button) view.findViewById(R.id.confirm_ok);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.openSpotifyDeeplink(spotifySongId);
                }
            });

            alertDialog.setView(view);
            alertDialog.show();
        }

        private class SearchTrackCallback extends SpotifyCallback<TracksPager> {

            private SongInfo songInfo;

            SearchTrackCallback(SongInfo songInfo) {
                this.songInfo = songInfo;
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                spotifyError.printStackTrace();
                songChecked.setVisibility(View.GONE);
            }

            @Override
            public void success(TracksPager tracksPager, Response response) {
                if (tracksPager != null) {
                    for (Track track : tracksPager.tracks.items) {
                        if (SpotifyUtils.isSameTrack(track, songInfo)) {
                            spotifySongId = track.id;
                            databaseAdapter.updateSpotifySongId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)), track.id);
                            presenter.checkIfSongSavedInSpotify(track.id, new SongCheckedSubscriber());
                            return;
                        }
                    }
                }
                songChecked.setVisibility(View.GONE);
            }
        }

        private class SongCheckedSubscriber extends SpotifyCallback<boolean[]> {
            @Override
            public void success(boolean[] booleans, Response response) {
                if (booleans.length > 0 && booleans[0]) {
                    songChecked.setVisibility(View.VISIBLE);
                } else {
                    songChecked.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                spotifyError.printStackTrace();
                songChecked.setVisibility(View.GONE);
            }
        }
    }
}
