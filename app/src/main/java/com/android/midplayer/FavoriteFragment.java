package com.android.midplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

public class FavoriteFragment extends Fragment implements FavoriteSongAdapter.OnSongClickListener, FavoriteSongAdapter.OnFavoriteToggleListener {
    private RecyclerView recyclerView;
    private FavoriteSongAdapter favoriteSongAdapter;
    // This list will hold the songs fetched from your static method
    private List<AudioTrack> favoriteSongs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.favoritesRecyclerView);

        // Load the songs using your provided static method
        // Note: This list must be modifiable, so I'm wrapping it if it isn't.
        // Your getFavoriteSongs() already returns a new ArrayList, so this is safe.
        favoriteSongs = SongsLibrary.getFavoriteSongs();

        favoriteSongAdapter = new FavoriteSongAdapter(favoriteSongs, this, this);
        recyclerView.setAdapter(favoriteSongAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Re-loads the favorite songs when the fragment becomes visible.
     * This ensures that if a song is un-favorited elsewhere,
     * it disappears from this list.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Reload the list from the "source of truth"
        favoriteSongs.clear();
        favoriteSongs.addAll(SongsLibrary.getFavoriteSongs());

        // Notify the adapter that the data has completely changed
        if (favoriteSongAdapter != null) {
            favoriteSongAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handles playing the song when an item is clicked.
     */
    @Override
    public void onSongClick(AudioTrack audioTrack, int position) {
        if (getActivity() == null) return;

        Intent playerIntent = new Intent(getActivity(), PlayMedia.class);
        // We pass the fragment's current list of favorite songs
        playerIntent.putExtra("SONG_LIST", (Serializable) favoriteSongs);
        playerIntent.putExtra("CURRENT_SONG_POSITION", position);
        startActivity(playerIntent);
    }

    /**
     * Handles removing the song from the favorites.
     */
    @Override
    public void onFavoriteToggle(AudioTrack audioTrack, int position) {
        // 1. Update the state of the master AudioTrack object in memory
        audioTrack.setFavorite(false);

        // 2. Remove the song from the local list being used by the adapter
        favoriteSongs.remove(position);

        // 3. Notify the adapter of the removal. This is sufficient to update the list.
        favoriteSongAdapter.notifyItemRemoved(position);
    }
}
