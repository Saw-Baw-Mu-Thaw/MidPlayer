package com.android.midplayer;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<AudioTrack> songList;
    private final OnSongClickListener onSongClickListener;

    public SongAdapter(List<AudioTrack> songList, OnSongClickListener listener) {
        this.songList = songList;
        this.onSongClickListener = listener;
    }

    public SongAdapter(List<AudioTrack> songList) {
        this(songList, null);
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        AudioTrack currentSong = songList.get(position);
        holder.titleTextView.setText(currentSong.getTitle());
        holder.artistTextView.setText(currentSong.getArtist());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView artistTextView;

        public SongViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            artistTextView = itemView.findViewById(R.id.text_view_artist);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onSongClickListener != null) {
                    onSongClickListener.onSongClick(songList.get(position));
                }
            });
        }
    }
}