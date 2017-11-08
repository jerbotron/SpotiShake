package com.jerbotron_mac.spotisave.activities.home.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerbotron_mac.spotisave.R;
import com.jerbotron_mac.spotisave.utils.DeveloperUtils;
import com.squareup.picasso.Picasso;

import static com.jerbotron_mac.spotisave.data.SongInfo.COVER_ART_URL;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_ALBUM;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_ARTIST;
import static com.jerbotron_mac.spotisave.data.SongInfo.TRACK_TITLE;

public class HistoryListAdapter extends RecyclerView.Adapter<HistoryListAdapter.SongViewHolder> {

    private Cursor cursor;
    private Context context;

    public HistoryListAdapter(Cursor cursor, Context context) {
        this.cursor = cursor;
        this.context = context;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_info_card, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.bindCursor(cursor);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView coverArt;
        private TextView title;
        private TextView album;
        private TextView artist;

        private SongViewHolder(View view) {
            super(view);
            coverArt = (ImageView) view.findViewById(R.id.song_card_cover_art);
            title = (TextView) view.findViewById(R.id.song_card_title);
            album = (TextView) view.findViewById(R.id.song_card_album);
            artist = (TextView) view.findViewById(R.id.song_card_artist);
        }

        private void bindCursor(Cursor cursor) {
            String url = DeveloperUtils.prependHttp(cursor.getString(cursor.getColumnIndexOrThrow(COVER_ART_URL)));
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.spotify_logo)
                    .into(coverArt);
            title.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_TITLE)));
            artist.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_ARTIST)));
            album.setText(cursor.getString(cursor.getColumnIndexOrThrow(TRACK_ALBUM)));
        }
    }
}
