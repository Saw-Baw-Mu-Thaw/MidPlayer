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

import java.util.List;
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
    private List<AudioTrack> allSongs;
    private int TOTAL_SONGS;
    private TextView songNameMini, songNameBig;
    private ImageView musicPlayerGif; // EXO: Made this a class member for access in listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);

        songNameMini = findViewById(R.id.songNameMini);
        songNameBig = findViewById(R.id.songNameBig);

        allSongs = SongsLibrary.getInitialSongList();
        TOTAL_SONGS = allSongs.size();
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
        if (intent != null) {
            currentSongId = intent.getIntExtra("SONG_ID", -1);
            String songTitle = intent.getStringExtra("SONG_TITLE");
            String artistName = intent.getStringExtra("ARTIST_NAME");

            if (currentSongId != -1) {
                // This will now play the song AND update the text
                playSongById(currentSongId);

            } else {
                // Handle case where activity is launched directly
                if (songTitle == null) songTitle = "Unknown Title";
                if (artistName == null) artistName = "Unknown Artist";
                songNameMini.setText(songTitle);
                songNameBig.setText(songTitle);
            }

            // --- MODIFICATION ---
            // 5. This setup is now handled by playSongById,
            //    but we keep it as a fallback if no ID was passed.
            if (currentSongId == -1) {
                if (songTitle == null) songTitle = "Unknown Title";
                songNameMini.setText(songTitle);
                songNameBig.setText(songTitle);
            }
            if (currentSongId == -1) {
                if (songTitle == null) songTitle = "Unknown Title";
                songNameMini.setText(songTitle);
                songNameBig.setText(songTitle);
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

    // ADD THIS NEW HELPER METHOD
    private AudioTrack getTrackById(int songId) {
        // Loop through your list (which starts at index 0)
        for (AudioTrack track : allSongs) {
            // Find the track where the ID matches
            if (track.getId() == songId) {
                return track;
            }
        }
        // If no song is found (e.g., ID 0 or > 21), return null
        return null;
    }
    public void playSongById(int songId) {
        if (exoPlayer == null) {
            initializePlayer();
        }

        // --- MODIFICATION START ---
        // Use the helper method to get the song's details
        AudioTrack currentTrack = getTrackById(songId);

        // Check if the track was found
        if (currentTrack != null) {
            // Set the text for the new song
            songNameMini.setText(currentTrack.getTitle());
            songNameBig.setText(currentTrack.getTitle());
        } else {
            // Fallback in case of a bad ID
            songNameMini.setText("Unknown Title");
            songNameBig.setText("Unknown Title");
            Log.e("PlayMedia", "Could not find track with ID: " + songId);
            // You might want to stop playback here or play song 1
            return; // Exit if no song found
        }
        // --- MODIFICATION END ---


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
            // If the resource isn't found, also update the text
            songNameMini.setText("Resource Missing");
            songNameBig.setText("Resource Missing");
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