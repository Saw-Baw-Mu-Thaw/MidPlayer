package com.android.midplayer;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SongReaderWriter {
    private static List<AudioTrack> trackList;

    private static final String FILENAME = "songs.xml";

    public static void saveSongsToXml(Context context) {

        trackList = new ArrayList<>();
        trackList.add(new AudioTrack(1, "mellow", "qlowdy", "10/10/2025 9:46 AM", "Lo-fi", "youtube audio library"));
        trackList.add(new AudioTrack(2, "happier", "sakura-girl", "10/10/2025 9:46 AM", "Electronic", "youtube audio library"));
        trackList.add(new AudioTrack(3, "watershed-moment", "ferco", "10/10/2025 9:45 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(4, "take-me-higher", "liqwyd", "10/10/2025 9:44 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(5, "let-me-know", "balynt", "10/10/2025 9:51 AM", "Cinematic", "youtube audio library"));
        trackList.add(new AudioTrack(6, "peace-of-mind", "roa-music", "10/10/2025 9:54 AM", "Lo-fi", "youtube audio library"));
        trackList.add(new AudioTrack(7, "heroic", "alex-productions", "10/10/2025 10:01 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(8, "afterglow", "tokyo-music-walker", "10/10/2025 10:02 AM", "Ambient", "youtube audio library"));
        trackList.add(new AudioTrack(9, "smile", "scandinavianz", "10/10/2025 10:02 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(10, "home", "sakura-girl", "10/10/2025 10:02 AM", "Folk", "youtube audio library"));
        trackList.add(new AudioTrack(11, "orange", "next-route", "10/10/2025 10:03 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(12, "thingy-moe", "zaw-paing", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(13, "myay-pyant-thu-lay","lay-phyu", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(14, "ma-sone-thaw-lan","zaw-paing", "06/22/2012 10:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(15, "kabar-a-setset","bunny-phyoe","03/20/2019 9:46 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(16, "roar","katy-perry", "08/10/2013 5:20 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(17, "just-the-way-your-are", "bruno-mars", "07/20/2010 6:50 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(18,"birds-of-a-feather","billie-eilish", "05/17/2024 7:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(19,"believer", "imagine-dragons",  "02/01/2017 10:15 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(20,"you-belong-with-me","taylor-swift","04/18/2009 8:45 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(21,"die-with-a-smail","lady-gaga-ft-bruno-mars","07/04/2024 9:00 AM", "Pop", "youtube audio library"));


        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag(null, "tracks");

            for(AudioTrack track : trackList) {
                serializer.startTag(null, "track");
                serializer.attribute(null, "id", Integer.toString(track.getId()));

                serializer.startTag(null, "title");
                serializer.text(track.getTitle());
                serializer.endTag(null, "title");

                serializer.startTag(null, "artist");
                serializer.text(track.getArtist());
                serializer.endTag(null, "artist");

                serializer.startTag(null, "dateAdded");
                serializer.text(track.getDateAdded());
                serializer.endTag(null, "dateAdded");

                serializer.startTag(null, "genre");
                serializer.text(track.getGenre());
                serializer.endTag(null, "genre");

                serializer.startTag(null, "source");
                serializer.text(track.getSource());
                serializer.endTag(null, "source");

                serializer.endTag(null, "track");
            }

            serializer.endTag(null, "tracks");
            serializer.endDocument();

            FileOutputStream fileOutputStream = context.openFileOutput(FILENAME, MODE_PRIVATE);
            fileOutputStream.write(writer.toString().getBytes());
            fileOutputStream.close();

        }catch(IOException e) {
            Log.e("IO read error", "Error occurred while reading songs.xml");
        }
    }

    public static List<AudioTrack> readSongsXml(Context context) {
        trackList = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();

        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            parser.setInput(fis, null);

            int eventType = parser.getEventType();
            AudioTrack currentTrack = null;

            int id = 0;
            String title = "";
            String artist = "";
            String dateAdded = "";
            String genre = "";
            String source = "";

            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName;
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();

                        if("track".equals(tagName)){
                            id = Integer.parseInt(parser.getAttributeValue(null, "id"));
                        } else if ("title".equals(tagName)) {
                            parser.require(XmlPullParser.START_TAG, null, "title");
                            title = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "title");
                        } else if ("artist".equals(tagName)) {
                            parser.require(XmlPullParser.START_TAG, null, "artist");
                            artist = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "artist");
                        } else if ("dateAdded".equals(tagName)) {
                            parser.require(XmlPullParser.START_TAG, null, "dateAdded");
                            dateAdded = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "dateAdded");
                        } else if ("genre".equals(tagName)) {
                            parser.require(XmlPullParser.START_TAG, null, "genre");
                            genre = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "genre");
                        }else if("source".equals(tagName)){
                            parser.require(XmlPullParser.START_TAG, null, "source");
                            source = parser.nextText();
                            parser.require(XmlPullParser.END_TAG, null, "source");
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        tagName = parser.getName();
                        if("track".equals(tagName)) {
                            currentTrack = new AudioTrack(id, title, artist, dateAdded, genre, source);
                            trackList.add(currentTrack);
                            currentTrack = null;
                        }
                        break;
                }
                eventType = parser.next();
            }
            fis.close();
        }
        catch(Exception e) {
            Log.e("SongReaderWriter", "Error occurred while reading songs.xml");
        }
        return trackList;
    }

    public static boolean checkFileExists(Context context) {
        File file = context.getFileStreamPath(FILENAME);
        return file.exists();
    }

    public static List<AudioTrack> getPlaylists() {
        return trackList;
    }
}
