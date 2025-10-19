package com.android.midplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ArtistSongAdapter extends ArrayAdapter<AudioTrack> {

    List<AudioTrack> trackList;
    Context myContext;

    PlaylistDetailFragment.PlaylistDetailListener listener;

    public ArtistSongAdapter(@NonNull Context context, int resource, @NonNull List<AudioTrack> objects) {
        super(context, resource, objects);
        trackList = objects;
        myContext = context;
        if(context instanceof PlaylistDetailFragment.PlaylistDetailListener) {
            listener = (PlaylistDetailFragment.PlaylistDetailListener) context;
        }
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(myContext);
            convertView = inflater.inflate(R.layout.artist_song_item, parent, false);
        }

        TextView artistSongItemNameTextView = convertView.findViewById(R.id.artistSongItemNameTextView);
        artistSongItemNameTextView.setText(trackList.get(position).getTitle());

        LinearLayout artistSongItem = convertView.findViewById(R.id.artistSongItem);

        artistSongItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPlaylistDetailClick(trackList.toArray(new AudioTrack[0]), position);
            }
        });

        return convertView;
    }
}
