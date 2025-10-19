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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class ArtistSongsFragment extends Fragment {

    private PlaylistDetailFragment.PlaylistDetailListener listener;
    private String name;
    private Context myContext;


    public ArtistSongsFragment() {

    }

    public static ArtistSongsFragment newInstance(String name) {
        ArtistSongsFragment f= new ArtistSongsFragment();
        Bundle b = new Bundle();
        b.putString("name", name);
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
        return inflater.inflate(R.layout.fragment_artist_songs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView artistListView = view.findViewById(R.id.artistSongListView);

        if(getArguments() != null) {
            name = getArguments().getString("name");
        }
        TextView artistSongItemName = view.findViewById(R.id.artistSongTextView);
        artistSongItemName.setText(name);

        List<AudioTrack> artistSongs = SongsLibrary.getSongsByArtist(name);

        ArrayAdapter myAdapter = new ArtistSongAdapter(myContext, R.layout.artist_song_item, artistSongs);
        artistListView.setAdapter(myAdapter);
    }
}