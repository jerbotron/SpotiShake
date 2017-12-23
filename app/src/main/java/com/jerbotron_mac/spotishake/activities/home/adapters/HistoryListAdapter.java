package com.jerbotron_mac.spotishake.activities.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.HomePresenter;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;
import com.jerbotron_mac.spotishake.shared.MaterialColor;
import com.jerbotron_mac.spotishake.utils.AppUtils;
import com.squareup.picasso.Picasso;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

import static android.provider.BaseColumns._ID;
import static com.jerbotron_mac.spotishake.data.SongInfo.CARD_COLOR;
import static com.jerbotron_mac.spotishake.data.SongInfo.COVER_ART_URL;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_ALBUM;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_ARTIST;
import static com.jerbotron_mac.spotishake.data.SongInfo.TRACK_TITLE;

public class HistoryListAdapter extends CursorRecyclerAdapter<HistoryListAdapter.SongViewHolder> {

    private Context context;
    private HomePresenter presenter;
    private DatabaseAdapter databaseAdapter;

    public HistoryListAdapter(Context context, HomePresenter presenter, DatabaseAdapter databaseAdapter) {
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

    public void saveSongToDb(GnResponseAlbums responseAlbums) {
        Observable.just(responseAlbums)
                .subscribe(new InsertRowSubscriber());
    }

    public void deleteSongFromDb(int position) {
        cursor.moveToPosition(position);
        Observable.just(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)))
                .subscribe(new DeleteRowSubscriber());
    }

    public void refreshCursor() {
        changeCursor(databaseAdapter.getCursor());
    }

    private class InsertRowSubscriber extends DisposableObserver<GnResponseAlbums> {
        @Override
        public void onNext(GnResponseAlbums row) {
            try {
                databaseAdapter.insertChanges(row);
                refreshCursor();
            } catch (GnException e) {
                onError(e);
            }
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

        private SongViewHolder(View view) {
            super(view);
            cardForeground = (RelativeLayout) view.findViewById(R.id.card_foreground);
            cardBackground = (RelativeLayout) view.findViewById(R.id.card_background);
            coverArt = (ImageView) view.findViewById(R.id.song_card_cover_art);
            title = (TextView) view.findViewById(R.id.song_card_title);
            album = (TextView) view.findViewById(R.id.song_card_album);
            artist = (TextView) view.findViewById(R.id.song_card_artist);
        }

        private void bindCursor(Cursor cursor) {
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
            }
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_TITLE)));
            artist.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_ARTIST)));
            album.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_ALBUM)));

            cardBackground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    presenter.openSpotifyDeeplink(title.getText().toString(),
                            artist.getText().toString(),
                            album.getText().toString());
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
    }
}
