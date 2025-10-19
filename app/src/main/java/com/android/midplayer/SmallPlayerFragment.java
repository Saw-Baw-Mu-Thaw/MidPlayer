package com.android.midplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmallPlayerFragment extends Fragment {

    public interface SmallPlayerListener {
        void onSmallPlayerClicked(AudioTrack[] tracks, int index, int duration);
    }

    private List<AudioTrack> tracks;
    private TextView smallPlayerTextView;
    private ImageButton smallPlayerPlayButton;
    private Context mainActContext;
    private SmallPlayerListener listener;

    private List<AudioTrack> pendingTracks;
    private BackgroundPlayerService backgroundPlayerService;
    private boolean isBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundPlayerService.MyBinder binder = (BackgroundPlayerService.MyBinder) iBinder;
            backgroundPlayerService = binder.getService();
            isBound = true;

            // If there are tracks waiting to be played, play them now.
            if (pendingTracks != null) {
                backgroundPlayerService.setListener(getContext());
                backgroundPlayerService.playSongs(pendingTracks);
                smallPlayerPlayButton.setImageResource(R.drawable.ic_pause);
                pendingTracks = null; // Clear the pending list
            }
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActContext = context;
        if (context instanceof SmallPlayerListener) {
            listener = (SmallPlayerListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_small_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        smallPlayerTextView = view.findViewById(R.id.smallPlayerTextView);
        smallPlayerPlayButton = view.findViewById(R.id.smallPlayerPlayButton);

        // Bind to the service
        Intent intent = new Intent(mainActContext, BackgroundPlayerService.class);
        mainActContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);

        smallPlayerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound && backgroundPlayerService != null) {
                    if (backgroundPlayerService.isPlaying()) {
                        backgroundPlayerService.pausePlayer();
                        smallPlayerPlayButton.setImageResource(R.drawable.ic_play_arrow);
                    } else {
                        backgroundPlayerService.startPlayer();
                        smallPlayerPlayButton.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });

        smallPlayerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBound && backgroundPlayerService != null && backgroundPlayerService.isPlaying()) {
                    AudioTrack[] trackArray = tracks.toArray(new AudioTrack[0]);
                    listener.onSmallPlayerClicked(trackArray, backgroundPlayerService.getIndex(),
                            backgroundPlayerService.getDuration());
                    backgroundPlayerService.pausePlayer();
                }
            }
        });
    }

    public void playSongs(AudioTrack[] tracks) {
        this.tracks = new ArrayList<>(Arrays.asList(tracks));

        if (isBound && backgroundPlayerService != null) {
            // Service is connected, play immediately.
            backgroundPlayerService.setListener(getContext());
            backgroundPlayerService.playSongs(this.tracks);
            smallPlayerPlayButton.setImageResource(R.drawable.ic_pause);
        } else {
            // Service is not connected yet, queue the tracks to be played on connection.
            pendingTracks = this.tracks;
        }
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
        super.onDestroy();
        // **CRITICAL FIX**: Always unbind from the service to prevent memory leaks.
        if (isBound) {
            if (backgroundPlayerService != null) {
                backgroundPlayerService.releaseListener();
            }
            mainActContext.unbindService(connection);
            isBound = false;
        }
    }
}