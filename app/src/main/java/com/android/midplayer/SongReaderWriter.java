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
        trackList.add(new AudioTrack(1, "Thingyan Moe", "Zaw Paing", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(2, "Myay Pyant Thu Lay","Lay Phyu", "09/10/2001 9:00 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(3, "Ma Sone Thaw Lan","Zaw Paing", "06/22/2012 10:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(4, "Kabar A Setset","Bunny Phyoe","03/20/2019 9:46 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(5, "Roar","Katy Perry", "08/10/2013 5:20 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(6, "Just The Way Your Are", "Bruno Mars", "07/20/2010 6:50 PM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(7,"Birds Of A Feather","Billie Eilish", "05/17/2024 7:30 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(8,"Believer", "Imagine Dragons",  "02/01/2017 10:15 AM", "Rock", "youtube audio library"));
        trackList.add(new AudioTrack(9,"You Belong With Me","Taylor Swift","04/18/2009 8:45 AM", "Pop", "youtube audio library"));
        trackList.add(new AudioTrack(10,"Die With A Smail","Lady Gaga ft Bruno Mars","07/04/2024 9:00 AM", "Pop", "youtube audio library"));

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
