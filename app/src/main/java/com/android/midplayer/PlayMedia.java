package com.android.midplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters; // EXO: Use ExoPlayer's parameters
import com.google.android.exoplayer2.Player; // EXO: Use ExoPlayer's base Player interface

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayMedia extends AppCompatActivity {

    // EXO: Changed from MediaPlayer to ExoPlayer
    private ExoPlayer exoPlayer;
    private ImageView playPauseButton;
    private ImageView nextSongButton;
    private ImageView prevSongButton;
    private SeekBar mediaSeekBar;

    private int currentSongId;
    private Spinner speedSpinner;
    private Handler seekBarHandler = new Handler();
    private TextView currentTimeText, durationTimeText;
    private final int TOTAL_SONGS = 11;
    private ImageView musicPlayerGif; // EXO: Made this a class member for access in listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);

        // EXO: Assign to class member
        musicPlayerGif = findViewById(R.id.musicPlayerGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.music_play)
                .into(musicPlayerGif);

        playPauseButton = findViewById(R.id.playPauseButton);
        nextSongButton = findViewById(R.id.skipForward);
        prevSongButton = findViewById(R.id.skipBack);
        mediaSeekBar = findViewById(R.id.mediaSeekBar);
        currentTimeText = findViewById(R.id.currentTimeText);
        durationTimeText = findViewById(R.id.durationTimeText);
        speedSpinner = (Spinner) findViewById(R.id.speedSelectionSpinner);

        // Spinner setup (no changes)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.playback_speeds,
                R.layout.custom_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        speedSpinner.setAdapter(adapter);

        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpeed = parent.getItemAtPosition(position).toString();
                float speed = 1.0f;
                switch (selectedSpeed) {
                    case "0.5x":
                        speed = 0.5f;
                        break;
                    case "1.5x":
                        speed = 1.5f;
                        break;
                    default:
                        speed = 1.0f;
                        break;
                }
                changePlayerSpeed(speed);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // EXO: Initialize ExoPlayer
        initializePlayer();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("SONG_ID")) {
            currentSongId = intent.getIntExtra("SONG_ID", -1);
            if (currentSongId != -1) {
                playSongById(currentSongId);
            }
        }

        playPauseButton.setOnClickListener(v -> {
            // EXO: Simplified play/pause logic. The listener will handle UI updates.
            if (exoPlayer != null) {
                if (exoPlayer.isPlaying()) {
                    exoPlayer.pause();
                } else {
                    exoPlayer.play();
                }
            }
        });

        mediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && exoPlayer != null) {
                    // EXO: Use exoPlayer.seekTo
                    exoPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        nextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer != null) { // EXO: Check exoPlayer
                    currentSongId++;
                    if (currentSongId > TOTAL_SONGS) {
                        currentSongId = 1;
                    }
                    playSongById(currentSongId);
                }
            }
        });

        prevSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exoPlayer != null) { // EXO: Check exoPlayer
                    currentSongId--;
                    if (currentSongId == 0) {
                        currentSongId = TOTAL_SONGS;
                    }
                    playSongById(currentSongId);
                }
            }
        });
    }

    // EXO: New method to initialize the player and set up its listener
    private void initializePlayer() {
        exoPlayer = new ExoPlayer.Builder(this).build();
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    // When ready, update duration and start seekbar
                    mediaSeekBar.setMax((int) exoPlayer.getDuration());
                    durationTimeText.setText(formatDuration(exoPlayer.getDuration()));
                    updateSeekBar.run();
                } else if (playbackState == Player.STATE_ENDED) {
                    // When completed, reset UI
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                    mediaSeekBar.setProgress(0);
                    currentTimeText.setText("0:00");
                    seekBarHandler.removeCallbacks(updateSeekBar);
                    // Go to next song automatically
                    nextSongButton.performClick();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                // This single callback handles all UI updates for play/pause
                if (isPlaying) {
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                    seekBarHandler.postDelayed(updateSeekBar, 1000);
                    Glide.with(PlayMedia.this)
                            .asGif()
                            .load(R.drawable.music_play)
                            .into(musicPlayerGif);
                } else {
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                    seekBarHandler.removeCallbacks(updateSeekBar);
                    Glide.with(PlayMedia.this).clear(musicPlayerGif);
                }
            }
        });
    }

    public void playSongById(int songId) {
        if (exoPlayer == null) {
            initializePlayer();
        }

        // EXO: Build a URI for the raw resource
        String resourceName = "song" + songId;
        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());

        if (resourceId != 0) {
            Uri mediaUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
            // EXO: Create a MediaItem
            MediaItem mediaItem = MediaItem.fromUri(mediaUri);

            // EXO: Set the item, prepare, and play
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
            // All UI updates (duration, button icon) are now handled by the listener
        } else {
            Log.e("ExoPlayer", "Raw resource not found for song ID: " + songId);
        }
    }

    private Runnable updateSeekBar = new Runnable() {
        public void run() {
            // EXO: Check exoPlayer and isPlaying
            if (exoPlayer != null && exoPlayer.isPlaying()) {
                long currentPosition = exoPlayer.getCurrentPosition();
                mediaSeekBar.setProgress((int) currentPosition);
                currentTimeText.setText(formatDuration(currentPosition));
                seekBarHandler.postDelayed(this, 1000);
            }
        }
    };

    private String formatDuration(long duration) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        // EXO: Release exoPlayer
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
        seekBarHandler.removeCallbacks(updateSeekBar);
        super.onDestroy();
    }

    private void changePlayerSpeed(float speed) {
        // EXO: Removed the API level check, ExoPlayer handles this
        try {
            if (exoPlayer != null) {
                // EXO: Use ExoPlayer's PlaybackParameters
                PlaybackParameters params = new PlaybackParameters(speed, 1.0f); // (speed, pitch)
                exoPlayer.setPlaybackParameters(params);
            }
        } catch (Exception e) {
            Log.e("ExoPlayer", "Failed to set playback speed: " + e.getMessage());
        }
    }
}