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
import android.widget.TextView;

import java.util.List;

public class GenreSongFragment extends Fragment {

    private PlaylistDetailFragment.PlaylistDetailListener listener;
    private String genre;
    private Context myContext;


    public GenreSongFragment() {

    }

    public static GenreSongFragment newInstance(String genre) {
        GenreSongFragment f= new GenreSongFragment();
        Bundle b = new Bundle();
        b.putString("genre", genre);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myContext = context;
        if(context instanceof PlaylistDetailFragment.PlaylistDetailListener) {
            listener = (PlaylistDetailFragment.PlaylistDetailListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_genre_song, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView artistListView = view.findViewById(R.id.genreSongListView);

        if(getArguments() != null) {
            genre = getArguments().getString("genre");
        }
        TextView artistSongItemName = view.findViewById(R.id.genreSongTextView);
        artistSongItemName.setText(genre);

        List<AudioTrack> genreSongs = SongsLibrary.getSongsByGenre(genre);

        ArrayAdapter myAdapter = new GenreSongAdapter(myContext, R.layout.genre_song_item, genreSongs);
        artistListView.setAdapter(myAdapter);
    }


}