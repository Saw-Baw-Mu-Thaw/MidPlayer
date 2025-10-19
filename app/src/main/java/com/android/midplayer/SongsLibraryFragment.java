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

import java.util.ArrayList;
import java.util.List;

// This will be our self-contained list component
public class SongsLibraryFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter; // Assuming you have an adapter named MyAdapter

    private List<AudioTrack> allSongs;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_song_library, container, false);
    }

    // Inside MyListFragment.java
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the RecyclerView from the inflated view
        allSongs = SongsLibrary.getInitialSongList();
        recyclerView = view.findViewById(R.id.recyclerViewSongs);


        songAdapter = new SongAdapter(allSongs, this::onSongClick);

        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void onSongClick(AudioTrack audioTrack) {
        Intent intent = new Intent(getActivity(), PlayMedia.class);


        intent.putExtra("SONG_ID", audioTrack.getId());

        startActivity(intent);
    }


}