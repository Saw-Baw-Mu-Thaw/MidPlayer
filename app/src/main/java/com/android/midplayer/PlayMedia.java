package com.android.midplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayMedia extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView playPauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);

        playPauseButton = findViewById(R.id.playPauseButton);
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("SONG_ID")) {
            int songId = intent.getIntExtra("SONG_ID", -1);
            if (songId != -1) {
                playSongById(songId);
            }
        }

        playPauseButton.setOnClickListener(v -> {
            // Toggle play/pause for the current song
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                } else {
                    mediaPlayer.start();
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }


    public void playSongById(int songId) {
        // Stop and release any song that is currently playing
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        String resourceName = "song" + songId;

        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());

        if (resourceId != 0) {
            mediaPlayer = MediaPlayer.create(this, resourceId);
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
                playPauseButton.setImageResource(R.drawable.ic_play_arrow);
            });
        } else {
            Log.e("MediaPlayer", "Raw resource not found for song ID: " + songId);
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}