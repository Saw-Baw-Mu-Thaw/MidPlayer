package com.android.midplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SongsLibrary extends AppCompatActivity implements OnSongClickListener {
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<AudioTrack> allSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_library);

        allSongs = getInitialSongList();
        recyclerView = findViewById(R.id.recyclerViewSongs);


        songAdapter = new SongAdapter(allSongs, this);

        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // MODIFICATION 3: This method is called when a song in the adapter is clicked
    @Override
    public void onSongClick(AudioTrack audioTrack) {
       Intent intent = new Intent(SongsLibrary.this, PlayMedia.class);
       intent.putExtra("SONG_ID", audioTrack.getId());
        startActivity(intent);

    }

    private List<AudioTrack> getInitialSongList() {
        List<AudioTrack> trackList = new ArrayList<>();
        trackList.add(new AudioTrack(1, "mellow", "qlowdy", "10/10/2025 9:46 AM", "Lo-fi", "youtube audio library"));
        trackList.add(new AudioTrack(2, "happier", "sakura-girl", "10/10/2025 9:46 AM", "Electronic", "youtube audio library"));
        trackList.add(new AudioTrack(3, "watershed-moment", "ferco", "10/10/2025 9:45 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(4, "take-me-higher", "liqwyd", "10/10/2025 9:44 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(5, "let-me-know", "balynt", "10/10/2025 9:51 AM", "Cinematic", "youtube audio library"));
        trackList.add(new AudioTrack(6, "peace-of-mind", "roa-music", "10/10/2025 9:54 AM", "Lo-fi", "youtube audio library"));
        trackList.add(new AudioTrack(7, "heroic", "alex-productions", "10/10/2025 10:01 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(8, "afterglow", "tokyo-music-walker", "10/10/2025 10:02 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(9, "smile", "scandinavianz", "10/10/2025 10:02 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(10, "home", "sakura-girl", "10/10/2025 10:02 AM", "Folk", "youtube audio library"));
        trackList.add(new AudioTrack(11, "orange", "next-route", "10/10/2025 10:03 AM", "Pop", "youtube audio library"));
        return trackList;
    }

    // Saw Baw Mu Thaw Code

}