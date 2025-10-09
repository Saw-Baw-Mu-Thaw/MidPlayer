package com.android.midplayer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // 2. Find the button in the layout
        Button startPlayerButton = findViewById(R.id.start_player_button);

        // 3. Set the click listener to navigate to the PlayMedia screen
        startPlayerButton.setOnClickListener(v -> {
            // Create an Intent to start the PlayMedia activity
            Intent playMediaIntent = new Intent(MainActivity.this, PlayMedia.class);

            // Start the new activity
            startActivity(playMediaIntent);

            // NOTE: Do NOT call finish() here, as you want the user to be
            // able to use the Back button to return to the MainActivity.
        });
    }
}