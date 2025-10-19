package com.android.midplayer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager; // Still useful, though ExoPlayer handles wakelock better
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BackgroundPlayerService extends Service {

    // NOTE: You must add the ExoPlayer dependencies to your build.gradle file:
    // implementation 'com.google.android.exoplayer:exoplayer:2.19.1' // Check for latest version

    public interface bgPlayerListener {
        void onPlay(String trackName);
        void onFinish();
    }

    private final IBinder myBinder = new MyBinder();
    private ExoPlayer exoPlayer = null; // Replaced MediaPlayer
    private List<AudioTrack> tracks;
    private int index;
    private bgPlayerListener listener;

    private final String CHANNEL_ID = "BackgroundPlayerService";
    private final int NOTIFICATION_ID = 1133;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private void initializeExoPlayer() {
        if (exoPlayer == null) {
            // Initialize ExoPlayer
            exoPlayer = new ExoPlayer.Builder(this).build();
            // Note: ExoPlayer handles PARTIAL_WAKE_LOCK automatically when playing audio
            // and the app is in the background, so PowerManager setup is less critical.
            // exoPlayer.setWakeMode(PowerManager.PARTIAL_WAKE_LOCK); // Not directly supported/needed

            // Set up a Listener for playback events
            exoPlayer.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    // Handle track completion
                    if (playbackState == Player.STATE_ENDED) {
                        // Check if there's a next track. ExoPlayer's queue handles this,
                        // but we keep the logic for listener notification and playlist end.
                        if (exoPlayer.hasNextMediaItem()) {
                            // ExoPlayer automatically advances, update index for service state
                            index = exoPlayer.getCurrentMediaItemIndex();
                            if (listener != null && index < tracks.size()) {
                                listener.onPlay(tracks.get(index).getTitle());
                            }
                        } else {
                            // Playlist finished
                            if (listener != null) {
                                listener.onFinish();
                            }
                            stopSelf();
                        }
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    // Notify listener when playback starts after prepared/loaded
                    if (isPlaying && listener != null && index < tracks.size() && exoPlayer.getPlaybackState() == Player.STATE_READY) {
                        listener.onPlay(tracks.get(index).getTitle());
                    }
                }

                @Override
                public void onMediaItemTransition(@NonNull MediaItem mediaItem, int reason) {
                    // Update index when track changes (e.g., auto-advance or seek)
                    index = exoPlayer.getCurrentMediaItemIndex();
                    // We notify listener in onIsPlayingChanged or STATE_ENDED check
                }
            });
        } else {
            // Reset the player
            exoPlayer.stop();
            exoPlayer.clearMediaItems();
        }
    }

    private void playTracks(int startIndex, long startPositionMs) {
        if (tracks == null || tracks.isEmpty() || exoPlayer == null) {
            return;
        }

        List<MediaItem> mediaItems = new ArrayList<>();
        for (AudioTrack track : tracks) {
            try {
                String id = "song" + track.getId();
                int resourceId = getResources().getIdentifier(id, "raw", getPackageName());
                if (resourceId != 0) {
                    Uri trackUri = RawResourceDataSource.buildRawResourceUri(resourceId);
                    MediaItem mediaItem = MediaItem.fromUri(trackUri);
                    mediaItems.add(mediaItem);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mediaItems.isEmpty()) {
            Toast.makeText(this, "No valid tracks found to play.", Toast.LENGTH_SHORT).show();
            return;
        }

        exoPlayer.setMediaItems(mediaItems, startIndex, startPositionMs);
        exoPlayer.prepare();
        exoPlayer.play(); // Start playback immediately
        this.index = startIndex; // Update the service's index state
    }

    public void playSongs(List<AudioTrack> tracks) {
        this.tracks = tracks;
        initializeExoPlayer();
        playTracks(0, 0); // Start from the beginning of the first track
    }

    public void playSongs(List<AudioTrack> t, int i, int duration) {
        this.tracks = t;
        initializeExoPlayer();
        playTracks(i, duration); // Start from track 'i' at 'duration' milliseconds
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification n = createNotification();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Notification permission not granted", Toast.LENGTH_SHORT).show();
            return START_STICKY;
        }

        // Use ServiceCompat for starting foreground service
        ServiceCompat.startForeground(this,
                NOTIFICATION_ID,
                n,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
        super.onDestroy();
    }

    // --- Player Control Methods ---
    public void pausePlayer() {
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
        }
        stopForeground(false); // Stop being a foreground service, but don't remove notification yet
    }

    public void startPlayer() {
        if (exoPlayer != null && !exoPlayer.isPlaying()) {
            exoPlayer.play();
            startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    public boolean isPlaying() {
        return exoPlayer != null && exoPlayer.isPlaying();
    }

    public int getIndex() { return index; }
    public int getDuration() { return exoPlayer != null ? (int) exoPlayer.getCurrentPosition() : 0; } // ExoPlayer uses long for position
    public List<AudioTrack> getPlaylist() { return tracks; }


    // --- Listener and Binder ---
    public void setListener(Context context) {
        if(context instanceof bgPlayerListener) {
            listener = (bgPlayerListener) context;
            // Optionally, call onPlay immediately if already playing
            if (isPlaying() && index < tracks.size()) {
                listener.onPlay(tracks.get(index).getTitle());
            }
        }
    }

    public void releaseListener() {
        listener = null;
    }

    public class MyBinder extends Binder {
        BackgroundPlayerService getService() {
            return BackgroundPlayerService.this;
        }
    }


    // --- Notification Methods ---
    public void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MidPlayer", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("MidPlayer app is playing music");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public Notification createNotification() {
        // You'll typically want a MediaStyle notification with ExoPlayer
        // but for a minimal replacement, we reuse the basic NotificationCompat setup.
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("MidPlayer")
                .setContentText(exoPlayer != null && index < tracks.size() ? "Playing: " + tracks.get(index).getTitle() : "Playing Music")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }
}