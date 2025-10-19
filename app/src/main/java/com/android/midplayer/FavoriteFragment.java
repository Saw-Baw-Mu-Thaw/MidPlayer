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

    private List<AudioTrack> favoriteSongs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.favoritesRecyclerView);


        favoriteSongs = SongsLibrary.getFavoriteSongs();

        favoriteSongAdapter = new FavoriteSongAdapter(favoriteSongs, this, this);
        recyclerView.setAdapter(favoriteSongAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void onResume() {
        super.onResume();


        favoriteSongs.clear();
        favoriteSongs.addAll(SongsLibrary.getFavoriteSongs());


        if (favoriteSongAdapter != null) {
            favoriteSongAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onSongClick(AudioTrack audioTrack, int position) {
        if (getActivity() == null) return;

        Intent playerIntent = new Intent(getActivity(), PlayMedia.class);
        // We pass the fragment's current list of favorite songs
        playerIntent.putExtra("SONG_LIST", (Serializable) favoriteSongs);
        playerIntent.putExtra("CURRENT_SONG_POSITION", position);
        startActivity(playerIntent);
    }


    @Override
    public void onFavoriteToggle(AudioTrack audioTrack, int position) {

        audioTrack.setFavorite(false);


        favoriteSongs.remove(position);


        favoriteSongAdapter.notifyItemRemoved(position);
    }
}
