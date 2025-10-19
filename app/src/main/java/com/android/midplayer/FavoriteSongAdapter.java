package com.android.midplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteSongAdapter extends RecyclerView.Adapter<FavoriteSongAdapter.FavoriteSongViewHolder> {

    private final List<AudioTrack> songs;
    private final OnSongClickListener songClickListener;
    private final OnFavoriteToggleListener favoriteToggleListener;

    public interface OnSongClickListener {
        void onSongClick(AudioTrack audioTrack, int position);
    }

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(AudioTrack audioTrack, int position);
    }

    public FavoriteSongAdapter(List<AudioTrack> songs, OnSongClickListener songClickListener, OnFavoriteToggleListener favoriteToggleListener) {
        this.songs = songs;
        this.songClickListener = songClickListener;
        this.favoriteToggleListener = favoriteToggleListener;
    }

    @NonNull
    @Override
    public FavoriteSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_favorite_song, parent, false);
        return new FavoriteSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteSongViewHolder holder, int position) {
        AudioTrack song = songs.get(position);
        holder.bind(song, songClickListener, favoriteToggleListener);
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    static class FavoriteSongViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        ImageView favoriteButton;

        public FavoriteSongViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.songTitleTextView);
            artistTextView = itemView.findViewById(R.id.songArtistTextView);
            favoriteButton = itemView.findViewById(R.id.removeFavoriteButton);
        }

        public void bind(final AudioTrack song, final OnSongClickListener songListener, final OnFavoriteToggleListener favListener) {
            titleTextView.setText(song.getTitle());
            artistTextView.setText(song.getArtist());


            itemView.setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    songListener.onSongClick(song, currentPosition);
                }
            });

            favoriteButton.setOnClickListener(v -> {
                int currentPosition = getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    favListener.onFavoriteToggle(song, currentPosition);
                }
            });
        }
    }
}
