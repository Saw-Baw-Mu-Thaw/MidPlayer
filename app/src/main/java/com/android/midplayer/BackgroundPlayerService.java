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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;

import java.util.List;

public class BackgroundPlayerService extends Service {

    public interface bgPlayerListener {
        void onPlay(String trackName);
        void onFinish();
    }

    private final IBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer = null;
    private List<AudioTrack> tracks;
    private int index;
    private bgPlayerListener listener;

    private final String CHANNEL_ID = "BackgroundPlayerService";
    private final int NOTIFICATION_ID = 1133;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private void initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            mediaPlayer.setOnCompletionListener(mp -> {
                if (index + 1 < tracks.size()) {
                    index++;
                    playTrackAtIndex(); // Play the next track in the list
                } else {
                    if (listener != null) {
                        listener.onFinish();
                    }
                    // Consider stopping the service or just releasing the player
                    stopSelf();
                }
            });

            mediaPlayer.setOnPreparedListener(mp -> {
                if (listener != null) {
                    listener.onPlay(tracks.get(index).getTitle());
                }
                mp.start();
            });
        } else {
            mediaPlayer.reset();
        }
    }

    private void playTrackAtIndex() {
        if (tracks == null || index < 0 || index >= tracks.size() || mediaPlayer == null) {
            return;
        }
        try {
            String id = "song" + tracks.get(index).getId();
            int resourceId = getResources().getIdentifier(id, "raw", getPackageName());
            if (resourceId == 0) return; // Resource not found
            Uri trackUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
            mediaPlayer.setDataSource(getApplicationContext(), trackUri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSongs(List<AudioTrack> tracks) {
        this.tracks = tracks;
        this.index = 0;
        initializeMediaPlayer();
        playTrackAtIndex();
    }

    public void playSongs(List<AudioTrack> t, int i, int duration) {
        this.tracks = t;
        this.index = i;
        initializeMediaPlayer();

        // Overwrite the onPrepared listener to handle seekTo
        mediaPlayer.setOnPreparedListener(mp -> {
            if (listener != null) {
                listener.onPlay(tracks.get(index).getTitle());
            }
            mp.seekTo(duration);
            mp.start();
        });

        playTrackAtIndex();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification n = createNotification();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Notification permission not granted", Toast.LENGTH_SHORT).show();
            return START_STICKY;
        }

        ServiceCompat.startForeground(this,
                NOTIFICATION_ID,
                n,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    // --- Player Control Methods ---
    public void pausePlayer() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        stopForeground(false); // Stop being a foreground service, but don't remove notification yet
    }

    public void startPlayer() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            startForeground(NOTIFICATION_ID, createNotification());
        }
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getIndex() { return index; }
    public int getDuration() { return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0; }
    public List<AudioTrack> getPlaylist() { return tracks; }


    // --- Listener and Binder ---
    public void setListener(Context context) {
        if(context instanceof bgPlayerListener) {
            listener = (bgPlayerListener) context;
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
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle("MidPlayer")
                .setContentText("Playing Music")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }
}