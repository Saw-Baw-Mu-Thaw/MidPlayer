package com.android.midplayer;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class GenreFragment extends Fragment {

    public interface GenreFragListener {
        public void onGenreClick(String genre);
    }

    ListView playlistListView;
    List<AudioTrack> trackList;
    Context mainActContext;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trackList = SongsLibrary.getInitialSongList();

        playlistListView = view.findViewById(R.id.genreListView);

        List<String> genres = new ArrayList<String>();
        for(AudioTrack t : trackList) {
            if(!genres.contains(t.getGenre())) {
                genres.add(t.getGenre());
            }
        }

        // reuse artist adapter for genre fragment
        ListAdapter adapter = new GenreAdapter(mainActContext,R.layout.fragment_genre, genres);
        playlistListView.setAdapter(adapter);
    }
}