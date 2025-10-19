package com.android.midplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements OnSongClickListener {

    private EditText searchEditText;
    private ImageButton searchButton;
    private RecyclerView searchResultsRecyclerView;

    private SongAdapter songAdapter;
    private List<AudioTrack> allSongs;
    private List<AudioTrack> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 1. Find all views
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);

        // 2. Load the full song list
        allSongs = SongsLibrary.getInitialSongList();

        // 3. Initialize the search results list and adapter
        searchResults = new ArrayList<>();
        songAdapter = new SongAdapter(searchResults, this);

        // 4. Setup the RecyclerView
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsRecyclerView.setAdapter(songAdapter);

        // 5. Set click listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        // Get the search query, convert to lowercase for case-insensitive search
        String query = searchEditText.getText().toString().toLowerCase().trim();

        // Clear the previous search results
        searchResults.clear();

        // If the query is empty, show no results
        if (query.isEmpty()) {
            songAdapter.notifyDataSetChanged();
            return;
        }

        // Loop through the full list of songs
        for (AudioTrack track : allSongs) {
            // Check for a match in title, artist, or genre
            if (track.getTitle().toLowerCase().contains(query) ||
                    track.getArtist().toLowerCase().contains(query) ||
                    track.getGenre().toLowerCase().contains(query))
            {
                // If a match is found, add it to the results list
                searchResults.add(track);
            }
        }

        // Notify the adapter that the data has changed to refresh the RecyclerView
        songAdapter.notifyDataSetChanged();
    }

    /**
     * This method is called when a user clicks on a song in the search results.
     * It implements the OnSongClickListener interface.
     */
    @Override
    public void onSongClick(AudioTrack audioTrack) {
        // Start the PlayMedia activity with the selected song's details
        Intent intent = new Intent(SearchActivity.this, PlayMedia.class);
        intent.putExtra("SONG_ID", audioTrack.getId());
        intent.putExtra("SONG_TITLE", audioTrack.getTitle());
        intent.putExtra("ARTIST_NAME", audioTrack.getArtist());
        startActivity(intent);
    }
}