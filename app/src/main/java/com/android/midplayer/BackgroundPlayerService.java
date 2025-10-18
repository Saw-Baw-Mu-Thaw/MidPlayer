package com.android.midplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.midi.MidiDeviceService;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

import kotlin._Assertions;

public class BackgroundPlayerService extends Service {

    public interface bgPlayerListener {
        public void onPlay(String trackName);
        public void onFinish();
    }

    private final IBinder myBinder = new MyBinder();
    private MediaPlayer mediaPlayer = null;
    private List<AudioTrack> tracks;
    private int index;
    private bgPlayerListener listener;

    private final String CHANNEL_ID = "BackgroundPlayerService";

    public BackgroundPlayerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return myBinder;
    }


    public void playSongs(List<AudioTrack> tracks) {
        index = 0;
        this.tracks = tracks;
        String id = "song" + tracks.get(index).getId();
        int resourceId = getResources().getIdentifier(id, "raw", getPackageName());
        Uri trackUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(BackgroundPlayerService.this, trackUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(index+1 < tracks.size()) {
                        index++;
                        String id = "song" + tracks.get(index).getId();
                        int resourceId = getResources().getIdentifier(id, "raw", getPackageName());
                        Uri trackUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(BackgroundPlayerService.this, trackUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.prepareAsync();
                    }
                    else {
                        mediaPlayer.release();
                        mediaPlayer = null;
                        listener.onFinish();
                    }

                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    listener.onPlay(tracks.get(index).getTitle());
                    mediaPlayer.start();
                }
            });

            mediaPlayer.prepareAsync();
        }
    }

    public void playSongs(List<AudioTrack> t, int i, int duration) {
        index = i;
        this.tracks = t;
        String id = "song" + this.tracks.get(index).getId();
        int resourceId = getResources().getIdentifier(id, "raw", getPackageName());
        Uri trackUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(BackgroundPlayerService.this, trackUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (index + 1 < tracks.size()) {
                        index++;
                        String id = "song" + tracks.get(index).getId();
                        int resourceId = getResources().getIdentifier(id, "raw", getPackageName());
                        Uri trackUri = Uri.parse("android.resource://" + getPackageName() + "/" + resourceId);
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(BackgroundPlayerService.this, trackUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.prepareAsync();
                    } else {
                        mediaPlayer.release();
                        mediaPlayer = null;
                        listener.onFinish();
                    }

                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    listener.onPlay(tracks.get(index).getTitle());
                    mediaPlayer.seekTo(duration);
                    mediaPlayer.start();
                }
            });

            mediaPlayer.prepareAsync();
        }
    }


    public void pausePlayer() {
        mediaPlayer.pause();
    }

    public void startPlayer() {
        mediaPlayer.start();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public boolean isNull() { return mediaPlayer == null; }

    public int getIndex() {
        return index;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public List<AudioTrack> getPlaylist() {
        return tracks;
    }

    public void setListener(Context context) {
        if(listener != null) {
            listener = null;
        }
        if(context instanceof bgPlayerListener) {
            listener = (bgPlayerListener) context;
        }
    }

    public void releaseListener() {
        listener = null;
    }

    public class MyBinder extends Binder {
        BackgroundPlayerService getService(){
            return BackgroundPlayerService.this;
        }
    }

    @Override
    public boolean stopService(Intent name) {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return super.stopService(name);
    }
}