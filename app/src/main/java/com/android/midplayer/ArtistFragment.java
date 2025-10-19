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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {

    public interface ArtistFragListener {
        public void onArtistClick(String name);
    }
    ListView playlistListView;
    List<AudioTrack> trackList;
    Context mainActContext;

    public ArtistFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        trackList = SongsLibrary.getInitialSongList();

        playlistListView = view.findViewById(R.id.artistListView);

        List<String> names = new ArrayList<String>();
        for(AudioTrack t : trackList) {
            if(!names.contains(t.getArtist())) {
                names.add(t.getArtist());
            }
        }


        ListAdapter adapter = new ArtistAdapter(mainActContext,R.layout.fragment_artist, names);
        playlistListView.setAdapter(adapter);
    }
}