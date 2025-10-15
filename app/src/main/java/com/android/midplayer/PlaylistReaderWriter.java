package com.android.midplayer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.provider.MediaStore;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaylistReaderWriter {


    private static List<Playlist> myPlaylists;
    ;
    private static final String FILENAME = "playlists.xml";

    public static void savePlaylistsToXml(Context context) {
        myPlaylists = new ArrayList<>();
        AudioTrack[] songs = new AudioTrack[] {
                new AudioTrack(1, "mellow", "qlowdy", "10/10/2025 9:46 AM", "Lo-fi", "youtube audio library"),
                new AudioTrack(2, "happier", "sakura-girl", "10/10/2025 9:46 AM", "Electronic", "youtube audio library")
        };
        myPlaylists.add(new Playlist("Playlist 1", 2,songs));

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();


        try{
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "playlists");

            for (Playlist playlist : myPlaylists) {
                serializer.startTag(null, "playlist");
                serializer.attribute(null, "name", playlist.getName());

                serializer.startTag(null, "numberOfSongs");
                serializer.text(Integer.toString(playlist.getNumber_of_songs()));
                serializer.endTag(null, "numberOfSongs");

                for (AudioTrack track : playlist.getSong_ids()){
                    serializer.startTag(null, "audioTrack");
                    // id
                    serializer.startTag(null, "id");
                    serializer.text(Integer.toString(track.getId()));
                    serializer.endTag(null, "id");
                    // title
                    serializer.startTag(null, "title");
                    serializer.text(track.getTitle());
                    serializer.endTag(null, "title");
                    // artist
                    serializer.startTag(null, "artist");
                    serializer.text(track.getArtist());
                    serializer.endTag(null, "artist");
                    // dateAdded
                    serializer.startTag(null, "dateAdded");
                    serializer.text(track.getDateAdded());
                    serializer.endTag(null, "dateAdded");
                    // genre
                    serializer.startTag(null, "genre");
                    serializer.text(track.getGenre());
                    serializer.endTag(null, "genre");
                    // source
                    serializer.startTag(null, "source");
                    serializer.text(track.getSource());
                    serializer.endTag(null, "source");

                    serializer.endTag(null, "audioTrack");
                }
                serializer.endTag(null, "playlist");
            }

            serializer.endTag(null, "playlists");
            serializer.endDocument();

            FileOutputStream fileOutputStream = context.openFileOutput(FILENAME, MODE_PRIVATE);
            fileOutputStream.write(writer.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePlaylistsToXml(Context context, List<Playlist> playlists) {
        myPlaylists = playlists;

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();


        try{
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "playlists");

            for (Playlist playlist : myPlaylists) {
                serializer.startTag(null, "playlist");
                serializer.attribute(null, "name", playlist.getName());

                serializer.startTag(null, "numberOfSongs");
                serializer.text(Integer.toString(playlist.getNumber_of_songs()));
                serializer.endTag(null, "numberOfSongs");

                for (AudioTrack track : playlist.getSong_ids()){
                    serializer.startTag(null, "audioTrack");
                    // id
                    serializer.startTag(null, "id");
                    serializer.text(Integer.toString(track.getId()));
                    serializer.endTag(null, "id");
                    // title
                    serializer.startTag(null, "title");
                    serializer.text(track.getTitle());
                    serializer.endTag(null, "title");
                    // artist
                    serializer.startTag(null, "artist");
                    serializer.text(track.getArtist());
                    serializer.endTag(null, "artist");
                    // dateAdded
                    serializer.startTag(null, "dateAdded");
                    serializer.text(track.getDateAdded());
                    serializer.endTag(null, "dateAdded");
                    // genre
                    serializer.startTag(null, "genre");
                    serializer.text(track.getGenre());
                    serializer.endTag(null, "genre");
                    // source
                    serializer.startTag(null, "source");
                    serializer.text(track.getSource());
                    serializer.endTag(null, "source");

                    serializer.endTag(null, "audioTrack");
                }
                serializer.endTag(null, "playlist");
            }

            serializer.endTag(null, "playlists");
            serializer.endDocument();

            FileOutputStream fileOutputStream = context.openFileOutput(FILENAME, MODE_PRIVATE);
            fileOutputStream.write(writer.toString().getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Playlist> readPlaylistsXml(Context context) {
        myPlaylists = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();

        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            parser.setInput(fis, null);

            int eventType = parser.getEventType();
            Playlist currentPlaylist = null;
            List<AudioTrack> currentSongs = null;

            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName;
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if("playlist".equals(tagName)) {
                            currentSongs = new ArrayList<AudioTrack>();
                            String name = parser.getAttributeValue(null, "name");
                            currentPlaylist = new Playlist(name, 0, new AudioTrack[0]);
                        } else if ("numberOfSongs".equals(tagName)) {
                            parser.require(XmlPullParser.START_TAG, null, "numberOfSongs");
                            currentPlaylist.setNumber_of_songs(Integer.parseInt(parser.nextText()));
                            parser.require(XmlPullParser.END_TAG, null, "numberOfSongs");
                        }else if ("audioTrack".equals(tagName)) {
                            // read a single audioTrack object

                            // id
                            parser.nextTag();
                            parser.require(XmlPullParser.START_TAG, null, "id");
                            int id = Integer.parseInt(parser.nextText());
                            parser.require(XmlPullParser.END_TAG, null, "id");
                            // title
                            parser.nextTag();
                            parser.require(XmlPullParser.START_TAG, null, "title");
                            String title = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "title");
                            // artist
                            parser.nextTag();
                            parser.require(XmlPullParser.START_TAG, null, "artist");
                            String artist = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "artist");
                            // dateAdded
                            parser.nextTag();
                            parser.require(XmlPullParser.START_TAG, null, "dateAdded");
                            String dateAdded = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "dateAdded");
                            // genre
                            parser.nextTag();
                            parser.require(XmlPullParser.START_TAG, null, "genre");
                            String genre = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "genre");
                            // source
                            parser.nextTag();
                            parser.require(XmlPullParser.START_TAG, null, "source");
                            String source = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "source");

                            AudioTrack temp = new AudioTrack(id, title, artist, dateAdded, genre, source);
                            currentSongs.add(temp);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if("playlist".equals(tagName)) {
                            AudioTrack[] temp = new AudioTrack[currentPlaylist.getNumber_of_songs()];
                            temp = currentSongs.toArray(temp);
                            currentPlaylist.setSong_ids(temp);
                            myPlaylists.add(currentPlaylist);
                            currentPlaylist = null;
                            currentSongs = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            fis.close();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }

        return myPlaylists;
    }

    public static boolean checkFileExists(Context context) {
        File file = context.getFileStreamPath(FILENAME);
        return file.exists();
    }

    public static List<Playlist> getPlaylists() {
        return myPlaylists;
    }
}
