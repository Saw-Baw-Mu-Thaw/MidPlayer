package com.android.midplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistFragment extends Fragment {

    public interface PlaylistFragListener {
        public void onPlaylistClick(Playlist playlist);
        public void onPlaylistRemove(int position);
        public void onPlayAllSongs(Playlist playlist);
        public void onPlaylistRenamed(List<Playlist> playlists);
        public void onPlaylistCreate(List<Playlist> playlists);
    }

    PlaylistFragListener listener;
    ListView playlistListView;
    GridView playlistGridView;

    ImageButton addPlaylistButton;
    boolean is_portrait;
    Context mainActContext;
    PlaylistListAdapter adapter;

    List<Playlist> playlists;



    public PlaylistFragment(){

    }

    public static PlaylistFragment newInstance(List<Playlist> playlists) {
        ArrayList<Playlist> list = new ArrayList<>(playlists);
        PlaylistFragment f = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putSerializable("playlists", list);
        f.setArguments(args);
        return f;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            playlists = (List<Playlist>) getArguments().getSerializable("playlists");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mainActContext = context;
        if (context instanceof PlaylistFragListener) {
            listener = (PlaylistFragListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            is_portrait = false;
        }
        else {
            is_portrait = true;
        }



        if(is_portrait){
            playlistListView = view.findViewById(R.id.playlistListView);

            adapter = new PlaylistListAdapter(mainActContext, R.layout.playlist_list_item, playlists);
            playlistListView.setAdapter(adapter);
        }
        else {
            playlistGridView = view.findViewById(R.id.playlistGridView);

            adapter = new PlaylistListAdapter(mainActContext, R.layout.playlist_grid_item, playlists);
            playlistGridView.setAdapter(adapter);
        }

        addPlaylistButton = view.findViewById(R.id.playlistCreateImageButton);
        addPlaylistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mainActContext);
                builder.setTitle("Create Playlist");

                EditText playlistNameEditText = new EditText(mainActContext);
                builder.setView(playlistNameEditText);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Playlist new_playlist = new Playlist(playlistNameEditText.getText().toString(), 0, new AudioTrack[0]);
                        playlists.add(new_playlist);
                        listener.onPlaylistCreate(playlists);
                        adapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                });

                builder.create().show();

            }
        });
    }

}