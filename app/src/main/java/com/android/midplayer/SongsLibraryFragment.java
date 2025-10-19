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
        allSongs = getInitialSongList();
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

    private List<AudioTrack> getInitialSongList() {
        List<AudioTrack> trackList = new ArrayList<>();
        trackList.add(new AudioTrack(1, "mellow", "qlowdy", "10/10/2025 9:46 AM", "Lo-fi", "youtube audio library"));
        trackList.add(new AudioTrack(2, "happier", "sakura-girl", "10/10/2025 9:46 AM", "Electronic", "youtube audio library"));
        trackList.add(new AudioTrack(3, "watershed-moment", "ferco", "10/10/2025 9:45 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(4, "take-me-higher", "liqwyd", "10/10/2025 9:44 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(5, "let-me-know", "balynt", "10/10/2025 9:51 AM", "Cinematic", "youtube audio library"));
        trackList.add(new AudioTrack(6, "peace-of-mind", "roa-music", "10/10/2025 9:54 AM", "Lo-fi", "youtube audio library"));
        trackList.add(new AudioTrack(7, "heroic", "alex-productions", "10/10/2025 10:01 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(8, "afterglow", "tokyo-music-walker", "10/10/2025 10:02 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(9, "smile", "scandinavianz", "10/10/2025 10:02 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(10, "home", "sakura-girl", "10/10/2025 10:02 AM", "Folk", "youtube audio library"));
        trackList.add(new AudioTrack(11, "orange", "next-route", "10/10/2025 10:03 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(12, "thingy-moe", "zaw-paing", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(13, "myay-pyant-thu-lay","lay-phyu", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(14, "ma-sone-thaw-lan","zaw-paing", "06/22/2012 10:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(15, "kabar-a-setset","bunny-phyoe","03/20/2019 9:46 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(16, "roar","katy-perry", "08/10/2013 5:20 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(17, "just-the-way-your-are", "bruno-mars", "07/20/2010 6:50 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(18,"birds-of-a-feather","billie-eilish", "05/17/2024 7:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(19,"believer", "imagine-dragons",  "02/01/2017 10:15 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(20,"you-belong-with-me","taylor-swift","04/18/2009 8:45 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(21,"die-with-a-smail","lady-gaga-ft-bruno-mars","07/04/2024 9:00 AM", "Pop", "youtube audio library"));
        return trackList;
    }
}