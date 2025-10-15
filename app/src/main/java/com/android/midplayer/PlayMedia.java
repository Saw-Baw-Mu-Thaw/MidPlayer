package com.android.midplayer;

import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayMedia extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView playPauseButton;
    private ImageView nextSongButton;
    private ImageView prevSongButton;
    private SeekBar mediaSeekBar;

    private int currentSongId;
    private Spinner speedSpinner;
    private Handler seekBarHandler = new Handler();
    private TextView currentTimeText, durationTimeText;
    private final int TOTAL_SONGS = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_media);

        playPauseButton = findViewById(R.id.playPauseButton);
        nextSongButton = findViewById(R.id.skipForward);
        prevSongButton = findViewById(R.id.skipBack);
        mediaSeekBar = findViewById(R.id.mediaSeekBar);
        currentTimeText = findViewById(R.id.currentTimeText);
        durationTimeText = findViewById(R.id.durationTimeText);
        speedSpinner = (Spinner) findViewById(R.id.speedSelectionSpinner);
        // 1. Create the ArrayAdapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.playback_speeds, // Your string array
                R.layout.custom_spinner_item // The custom layout you created
        );

        // 2. Specify the layout to use when the list of choices appears
        // You can reuse the same layout or create a separate one for the dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 3. Apply the adapter to the spinner
        speedSpinner.setAdapter(adapter);

        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSpeed = parent.getItemAtPosition(position).toString();
                Toast.makeText(PlayMedia.this, "Selected: " + selectedSpeed, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // This is usually left empty
            }
        });

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("SONG_ID")) {
            currentSongId = intent.getIntExtra("SONG_ID", -1);
            if (currentSongId != -1) {
                playSongById(currentSongId);
            }
        }

        playPauseButton.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                    seekBarHandler.removeCallbacks(updateSeekBar);
                } else {
                    mediaPlayer.start();
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                    updateSeekBar.run();
                }
            }
        });

        mediaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //implement next song function
        nextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    currentSongId++;
                    // If the next song ID exceeds the total, loop back to the first song
                    if (currentSongId > TOTAL_SONGS) {
                        currentSongId = 1;
                    }
                    playSongById(currentSongId);
                }
            }
        });
        //implement prev song function
        prevSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    currentSongId--;
                    if (currentSongId == 0) { // intent firstly check whether it is less than -1
                        currentSongId = TOTAL_SONGS;
                    }
                    playSongById(currentSongId);
                }
            }
        });
    }

    public void playSongById(int songId) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        String resourceName = "song" + songId;
        int resourceId = getResources().getIdentifier(resourceName, "raw", getPackageName());

        if (resourceId != 0) {
            mediaPlayer = MediaPlayer.create(this, resourceId);
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.ic_pause);

            mediaSeekBar.setMax(mediaPlayer.getDuration());
            durationTimeText.setText(formatDuration(mediaPlayer.getDuration()));
            updateSeekBar.run();

            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
                playPauseButton.setImageResource(R.drawable.ic_play_arrow);
                mediaSeekBar.setProgress(0);
                currentTimeText.setText("0:00");
                seekBarHandler.removeCallbacks(updateSeekBar);
            });
        } else {
            Log.e("MediaPlayer", "Raw resource not found for song ID: " + songId);
        }
    }

    private Runnable updateSeekBar = new Runnable() {
        public void run() {
            if (mediaPlayer != null) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                mediaSeekBar.setProgress(currentPosition);
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
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        seekBarHandler.removeCallbacks(updateSeekBar);
        super.onDestroy();
    }
}