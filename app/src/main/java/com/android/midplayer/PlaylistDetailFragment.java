package com.android.midplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDetailFragment extends Fragment {

    private Playlist playlist;
    private List<AudioTrack> trackList;

    private PlaylistDetailListener listener;

    private boolean isPortrait;

    ImageButton playlistDetailBackImageButton;
    ImageButton playlistDetailAddImageButton;
    TextView playlistDetailNameTextView;

    RecyclerView playlistDetailRecyclerView;

    RecyclerView.LayoutManager layoutManager;

    public interface PlaylistDetailListener {
        public void onPlaylistDetailClick(AudioTrack[] track, int index);
        public void onUpdatePlaylist(Playlist playlist);
    }

    public PlaylistDetailFragment() {
        // Required empty public constructor
    }

    public static PlaylistDetailFragment newInstance(Playlist playlist) {
        Bundle args = new Bundle();
        args.putSerializable("playlist", playlist);
        PlaylistDetailFragment fragment = new PlaylistDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            playlist = (Playlist) getArguments().getSerializable("playlist");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        trackList = SongReaderWriter.readSongsXml(context);
        if(context instanceof PlaylistDetailListener) {
            listener = (PlaylistDetailListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
        }else{
            isPortrait = false;
        }

        playlistDetailRecyclerView = view.findViewById(R.id.playlistDetailRecyclerView);
        if(isPortrait) {
            layoutManager = new LinearLayoutManager(getContext());
        }else{
            layoutManager = new GridLayoutManager(getContext(), 3);
        }

        playlistDetailRecyclerView.setLayoutManager(layoutManager);

        PlaylistDetailAdapter adapter = new PlaylistDetailAdapter(getContext(), playlist.getSong_ids());
        playlistDetailRecyclerView.setAdapter(adapter);

        playlistDetailAddImageButton = view.findViewById(R.id.playlistDetailAddImageButton);
        playlistDetailBackImageButton = view.findViewById(R.id.playlistDetailBackImageButton);
        playlistDetailNameTextView = view.findViewById(R.id.playlistDetailNameTextView);

        playlistDetailNameTextView.setText(playlist.getName());

        playlistDetailBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        playlistDetailAddImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean[] checked = new boolean[trackList.size()];
                ArrayList<Integer> selectedSongs = new ArrayList<>();
                for(AudioTrack playlistTrack : playlist.getSong_ids()) {
                    checked[playlistTrack.getId()-1] = true;
                    selectedSongs.add(playlistTrack.getId()-1);
                }

                String[] songNames = new String[trackList.size()];

                for(int i = 0; i < trackList.size(); i++) {
                    songNames[i] = trackList.get(i).getTitle();
                }




                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Add to Playlist");
                builder.setMultiChoiceItems(songNames, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean isChecked){
                        if(isChecked) {
                            selectedSongs.add(i);
                        }else if(selectedSongs.contains(i)){
                            // remove song
                            selectedSongs.remove((Integer) i);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int num) {
                        AudioTrack[] tracks = new AudioTrack[selectedSongs.size()];
                        int trackIndex = 0;
                        for(int i = 1; i <= trackList.size(); i++) {
                            if(selectedSongs.contains(i)){
                                tracks[trackIndex] = trackList.get(i);
                                trackIndex++;
                            }
                        }
                        playlist.setSong_ids(tracks);
                        playlist.setNumber_of_songs(tracks.length);
                        listener.onUpdatePlaylist(playlist);
                        adapter.setTracks(tracks);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //we do nothing
                    }
                });

                builder.show();

            }
        });


    }
}