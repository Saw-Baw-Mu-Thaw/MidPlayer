package com.android.midplayer;

import java.io.Serializable;

public class AudioTrack implements Serializable {
    private int id;
    private String title;
    private String artist;
    private String dateAdded;
    private String genre;
    private String source;

    public AudioTrack(int id, String title, String artist, String dateAdded, String genre, String source) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.dateAdded = dateAdded;
        this.genre = genre;
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getGenre() {
        return genre;
    }

    public String getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "AudioTrack{id=" + id + ", title='" + title + "', artist='" + artist + "', genre='" + genre + "'}";
    }
}

