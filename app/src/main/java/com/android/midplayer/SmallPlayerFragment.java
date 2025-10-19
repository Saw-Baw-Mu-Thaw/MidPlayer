package com.android.midplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SmallPlayerFragment extends Fragment{

    public interface SmallPlayerListener {
        public void onSmallPlayerClicked(AudioTrack[] tracks, int index);
    }
    private List<AudioTrack> tracks;
    private int index;
    private int duration;
    private TextView smallPlayerTextView;
    private ImageButton smallPlayerPlayButton;
    private Context mainActContext;
    private SmallPlayerListener listener;

    private BackgroundPlayerService backgroundPlayerService;

    private boolean isBound = false;

    final private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundPlayerService.MyBinder binder = (BackgroundPlayerService.MyBinder) iBinder;
            backgroundPlayerService = binder.getService();
            isBound = true;

            backgroundPlayerService.setListener(getContext());

            // can't save state to continue playback
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
            backgroundPlayerService = null;
        }
    };


    public SmallPlayerFragment() {
        // Required empty public constructor
    }

    public static SmallPlayerFragment newInstance() {
        return new SmallPlayerFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActContext = context;
        if(context instanceof SmallPlayerListener) {
            listener = (SmallPlayerListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_small_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smallPlayerTextView = view.findViewById(R.id.smallPlayerTextView);
        smallPlayerPlayButton = view.findViewById(R.id.smallPlayerPlayButton);

        Intent intent = new Intent(mainActContext, BackgroundPlayerService.class);
        mainActContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);


        smallPlayerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // play or stop music
                if(!backgroundPlayerService.isNull()) {
                    if(backgroundPlayerService.isPlaying()) {
                        // pause media player
                        backgroundPlayerService.pausePlayer();
                        smallPlayerPlayButton.setImageResource(R.drawable.ic_play_arrow);
                    }else{
                        // start media player
                        backgroundPlayerService.startPlayer();
                        smallPlayerPlayButton.setImageResource(R.drawable.ic_pause);
                    }
                }

            }
        });

        smallPlayerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!backgroundPlayerService.isNull() && backgroundPlayerService.isPlaying()) {
                    AudioTrack[] trackArray = tracks.toArray(new AudioTrack[0]);
                    listener.onSmallPlayerClicked(trackArray, backgroundPlayerService.getIndex());
                }

            }
        });


    }

    public void playSongs(AudioTrack[] tracks) {
        // BaseActivity can use this method to tell it to play songs in background
        this.tracks = new ArrayList<>();
        this.tracks.addAll(Arrays.asList(tracks));

        backgroundPlayerService.setListener(getContext());
        backgroundPlayerService.playSongs(this.tracks);

//        backgroundPlayerService.startPlayer();
        smallPlayerPlayButton.setImageResource(R.drawable.ic_pause);
    }

    public void setPlayerTitle(String name) {
        smallPlayerTextView.setText(name);
    }

    public void onSmallPlayerFinished() {
        smallPlayerTextView.setText("Nothing is playing");
        smallPlayerPlayButton.setImageResource(R.drawable.ic_play_arrow);
    }

    @Override
    public void onDestroy() {
        if(backgroundPlayerService != null && !backgroundPlayerService.isNull()) {
            backgroundPlayerService.releaseListener();
        }
        mainActContext.unbindService(connection);
        isBound = false;
        super.onDestroy();
    }

}