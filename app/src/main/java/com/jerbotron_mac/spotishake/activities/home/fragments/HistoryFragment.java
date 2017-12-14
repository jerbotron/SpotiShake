package com.jerbotron_mac.spotishake.activities.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gracenote.gnsdk.GnResponseAlbums;
import com.jerbotron_mac.spotishake.R;
import com.jerbotron_mac.spotishake.activities.home.HomeActivity;
import com.jerbotron_mac.spotishake.activities.home.adapters.HistoryListAdapter;
import com.jerbotron_mac.spotishake.activities.home.custom.RecyclerItemTouchHelper;
import com.jerbotron_mac.spotishake.data.DatabaseAdapter;

public class HistoryFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private LinearLayout fragmentLayout;

    private HistoryListAdapter historyListAdapter;

    private Context context;
    private DatabaseAdapter databaseAdapter;
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        fragmentLayout = (LinearLayout) view.findViewById(R.id.fragment_history_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.song_history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (historyListAdapter != null) {
            historyListAdapter.refreshCursor();
            recyclerView.setAdapter(historyListAdapter);
        }

        toolbar = (Toolbar) view.findViewById(R.id.history_toolbar);
        toolbar.setTitle(R.string.history_toolbar_title);
        toolbar.inflateMenu(R.menu.menu_history);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.menu_settings) {
                    ((HomeActivity) context).launchSettingsActivity();
                }
                return false;
            }
        });

        itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        this.context = context;

        if (historyListAdapter == null) {
            databaseAdapter.open();
            historyListAdapter = new HistoryListAdapter(context, databaseAdapter);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setDatabaseAdapter(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
    }

    public void refreshView() {
        historyListAdapter.refreshCursor();
        historyListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof HistoryListAdapter.SongViewHolder) {
            final View cardForeground = viewHolder.itemView.findViewById(R.id.card_foreground);
            final View cardBackground = viewHolder.itemView.findViewById(R.id.card_background);
            cardForeground.setVisibility(View.GONE);
            cardBackground.setVisibility(View.GONE);

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar.make(fragmentLayout, R.string.history_deleted, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.undo, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cardForeground.setVisibility(View.VISIBLE);
                    historyListAdapter.notifyItemChanged(position);
                    cardForeground.setVisibility(View.VISIBLE);
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.spotifyGreen));
            snackbar.show();

            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if (!cardForeground.isShown()) {
                        historyListAdapter.notifyItemRemoved(position);
                        historyListAdapter.deleteSongFromDb(position);
                    }
                }
            });
        }
    }

    public void saveSong(GnResponseAlbums responseAlbums) {
        historyListAdapter.saveSongToDb(responseAlbums);
    }

    private class EmptyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
