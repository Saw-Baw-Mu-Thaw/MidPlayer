package com.android.midplayer;

import static androidx.core.graphics.drawable.DrawableCompat.applyTheme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements PlaylistFragment.PlaylistFragListener {

    ImageView playlistButton;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    PlaylistFragment playlistFragment;
    List<Playlist> playlists;

    //account info
    ImageView ivAccount;
    String username, email;
    public static boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        applyTheme();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.baseActivityFragmentContainer, new SongsLibraryFragment())
                    .commit();
        }

        // write playlists xml file if it doesn't exist
        if (PlaylistReaderWriter.checkFileExists(BaseActivity.this)) {
            playlists = PlaylistReaderWriter.readPlaylistsXml(BaseActivity.this);
        }else{
            PlaylistReaderWriter.savePlaylistsToXml(BaseActivity.this);
            playlists = PlaylistReaderWriter.getPlaylists();
        }

        // setting up playlist fragment
        playlistButton = findViewById(R.id.musicLibrary);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        playlistFragment = PlaylistFragment.newInstance(playlists);

        // account info
        ivAccount = findViewById(R.id.ivAccount);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        ivAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BaseActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BaseActivity.this, AccountInfoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        playlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BaseActivity.this, "Playlist button clicked", Toast.LENGTH_SHORT).show();

                if (transaction.isEmpty()) {
                    transaction.replace(R.id.baseActivityFragmentContainer, playlistFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }

    private void applyTheme() {
        boolean isDarkMode = getSharedPreferences("theme", MODE_PRIVATE)
                .getBoolean("dark_mode", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onPlaylistClick(Playlist playlist) {
        // launch the playlist detail fragment
        Toast.makeText(BaseActivity.this, "Playlist clicked", Toast.LENGTH_SHORT).show();
    }

    public void onPlaylistRemove(int position) {
        // remove the playlist sent from here
        playlists.remove(position);
        PlaylistReaderWriter.savePlaylistsToXml(BaseActivity.this, playlists);
    }

    public void onPlayAllSongs(Playlist playlist) {
        // give media player to play all songs in the playlist
        Toast.makeText(BaseActivity.this, "Playing playlist", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaylistRenamed(List<Playlist> playlists) {
        PlaylistReaderWriter.savePlaylistsToXml(BaseActivity.this, playlists);
    }

    public void onPlaylistCreate(List<Playlist> playlists) {
        PlaylistReaderWriter.savePlaylistsToXml(BaseActivity.this, playlists);
    }
}


