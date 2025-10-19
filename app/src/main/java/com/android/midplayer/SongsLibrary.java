package com.android.midplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * A final, non-activity class that holds the master list of songs
 * and provides static methods to access them.
 */
public final class SongsLibrary {

    // 1. The one, single, master list of all songs for the entire app.
    private static final List<AudioTrack> MASTER_SONG_LIST;

    // 2. A static block initializes this list exactly once when the app starts.
    static {
        MASTER_SONG_LIST = new ArrayList<>();
        MASTER_SONG_LIST.add(new AudioTrack(1, "Thingyan Moe", "Zaw Paing", "09/10/2001 9:00 AM", "Rock", "youtube audio library", false));
        MASTER_SONG_LIST.add(new AudioTrack(2, "Myay Pyant Thu Lay","Lay Phyu", "09/10/2001 9:00 AM", "Rock", "youtube audio library", true));
        MASTER_SONG_LIST.add(new AudioTrack(3, "Ma Sone Thaw Lan","Zaw Paing", "06/22/2012 10:30 AM", "Pop", "youtube audio library", true));
        MASTER_SONG_LIST.add(new AudioTrack(4, "Kabar A Setset","Bunny Phyoe","03/20/2019 9:46 AM", "Pop", "youtube audio library", false));
        MASTER_SONG_LIST.add(new AudioTrack(5, "Roar","Katy Perry", "08/10/2013 5:20 PM", "Pop", "youtube audio library", false));
        MASTER_SONG_LIST.add(new AudioTrack(6, "Just The Way Your Are", "Bruno Mars", "07/20/2010 6:50 PM", "Pop", "youtube audio library", true));
        MASTER_SONG_LIST.add(new AudioTrack(7,"Birds Of A Feather","Billie Eilish", "05/17/2024 7:30 AM", "Pop", "youtube audio library", false));
        MASTER_SONG_LIST.add(new AudioTrack(8,"Believer", "Imagine Dragons",  "02/01/2017 10:15 AM", "Rock", "youtube audio library",false));
        MASTER_SONG_LIST.add(new AudioTrack(9,"You Belong With Me","Taylor Swift","04/18/2009 8:45 AM", "Pop", "youtube audio library",false));
        MASTER_SONG_LIST.add(new AudioTrack(10,"Die With A Smail","Lady Gaga ft Bruno Mars","07/04/2024 9:00 AM", "Pop", "youtube audio library", true));
    }

    /**
     * Private constructor to prevent anyone from creating an instance
     * of this utility class.
     */
    private SongsLibrary() {
    }

    /**
     * Gets the single, shared list of all songs.
     *
     * @return The static MASTER_SONG_LIST.
     */
    public static List<AudioTrack> getInitialSongList() {
        return MASTER_SONG_LIST;
    }

    /**
     * Creates a new list containing only the favorited songs
     * from the master list.
     *
     * @return A new ArrayList of favorited AudioTracks.
     */
    public static List<AudioTrack> getFavoriteSongs() {
        List<AudioTrack> favoriteSongs = new ArrayList<>();
        for (AudioTrack track : MASTER_SONG_LIST) {
            if (track.isFavorite()) {
                favoriteSongs.add(track);
            }
        }
        return favoriteSongs;
    }

    /**
     * Gets a new list of songs by a specific artist.
     */
    public static List<AudioTrack> getSongsByArtist(String artistName) {
        List<AudioTrack> artistSongs = new ArrayList<>();
        if (artistName == null || artistName.isEmpty()) {
            return artistSongs;
        }
        for (AudioTrack track : MASTER_SONG_LIST) {
            if (track.getArtist().equalsIgnoreCase(artistName)) {
                artistSongs.add(track);
            }
        }
        return artistSongs;
    }

    /**
     * Gets a new list of songs in a specific genre.
     */
    public static List<AudioTrack> getSongsByGenre(String genreName) {
        List<AudioTrack> genreSongs = new ArrayList<>();
        if (genreName == null || genreName.isEmpty()) {
            return genreSongs;
        }
        for (AudioTrack track : MASTER_SONG_LIST) {
            if (track.getGenre().equalsIgnoreCase(genreName)) {
                genreSongs.add(track);
            }
        }
        return genreSongs;
    }

    /**
     * Finds and returns a single song from the master list by its ID.
     *
     * @param songId The ID of the song to find.
     * @return The matching AudioTrack, or null if not found.
     */
    public static AudioTrack getTrackById(int songId) {
        for (AudioTrack track : MASTER_SONG_LIST) {
            if (track.getId() == songId) {
                return track;
            }
        }
        return null; // Not found
    }
}