package com.android.midplayer;

public class Playlist {
    private String name;
    private int number_of_songs;
    private AudioTrack[] song_ids;

    public Playlist(String name, int number_of_songs, AudioTrack[] song_ids){
        this.name = name;
        this.number_of_songs = number_of_songs;
        this.song_ids = song_ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber_of_songs() {
        return number_of_songs;
    }

    public void setNumber_of_songs(int number_of_songs) {
        this.number_of_songs = number_of_songs;
    }

    public AudioTrack[] getSong_ids() {
        return song_ids;
    }

    public void setSong_ids(AudioTrack[] song_ids) {
        this.song_ids = song_ids;
    }
}
