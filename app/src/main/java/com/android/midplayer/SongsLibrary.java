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
        /*
        recyclerView = findViewById(R.id.recyclerViewSongs);


        songAdapter = new SongAdapter(allSongs, this);

        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        */

         /*
         //Example calls For Baw Thaw, loop through it by getting artists list first then

         List<AudioTrack> zawPaingSongs = SongsLibrary.getSongsByArtist("Zaw Paing");

            // To get all "Rock" songs
            List<AudioTrack> rockSongs = SongsLibrary.getSongsByGenre("Rock");

         */
    }

    // MODIFICATION 3: This method is called when a song in the adapter is clicked
    @Override
    public void onSongClick(AudioTrack audioTrack) {
      /*
       Intent intent = new Intent(SongsLibrary.this, PlayMedia.class);
        intent.putExtra("SONG_ID", audioTrack.getId());
        intent.putExtra("SONG_TITLE", audioTrack.getTitle());
        intent.putExtra("ARTIST_NAME", audioTrack.getArtist());
        startActivity(intent);
       */

    }

    public static List<AudioTrack> getInitialSongList() {
        List<AudioTrack> trackList = new ArrayList<>();
        trackList.add(new AudioTrack(1, "Thingyan Moe", "Zaw Paing", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(2, "Myay Pyant Thu Lay","Lay Phyu", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(3, "Ma Sone Thaw Lan","Zaw Paing", "06/22/2012 10:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(4, "Kabar A Setset","Bunny Phyoe","03/20/2019 9:46 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(5, "Roar","Katy Perry", "08/10/2013 5:20 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(6, "Just The Way Your Are", "Bruno Mars", "07/20/2010 6:50 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(7,"Birds Of A Feather","Billie Eilish", "05/17/2024 7:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(8,"Believer", "Imagine Dragons",  "02/01/2017 10:15 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(9,"You Belong With Me","Taylor Swift","04/18/2009 8:45 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(10,"Die With A Smail","Lady Gaga ft Bruno Mars","07/04/2024 9:00 AM", "Pop", "youtube audio library"));
        return trackList;
    }

    public static List<AudioTrack> getSongsByArtist(String artistName) {
        List<AudioTrack> allSongs = getInitialSongList();
        List<AudioTrack> artistSongs = new ArrayList<>();

        if (artistName == null || artistName.isEmpty()) {
            return artistSongs; // Return empty list if no artist is specified
        }

        for (AudioTrack track : allSongs) {
            // Use equalsIgnoreCase for a case-insensitive match
            if (track.getArtist().equalsIgnoreCase(artistName)) {
                artistSongs.add(track);
            }
        }
        return artistSongs;
    }

    /**
     * Returns a list of all songs in a specific genre.
     *
     * @param genreName The name of the genre to search for.
     * @return A List of AudioTrack objects matching the genre.
     */
    public static List<AudioTrack> getSongsByGenre(String genreName) {
        List<AudioTrack> allSongs = getInitialSongList();
        List<AudioTrack> genreSongs = new ArrayList<>();

        if (genreName == null || genreName.isEmpty()) {
            return genreSongs; // Return empty list if no genre is specified
        }

        for (AudioTrack track : allSongs) {
            // Use equalsIgnoreCase for a case-insensitive match
            if (track.getGenre().equalsIgnoreCase(genreName)) {
                genreSongs.add(track);
            }
        }
        return genreSongs;
    }

    // Saw Baw Mu Thaw Code

}