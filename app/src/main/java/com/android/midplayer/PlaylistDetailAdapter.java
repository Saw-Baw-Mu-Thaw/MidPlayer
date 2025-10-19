package com.android.midplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder>{
    AudioTrack[] tracks;

    PlaylistDetailFragment.PlaylistDetailListener listener;

    public PlaylistDetailAdapter(Context context, AudioTrack[] tracks) {
        this.tracks = tracks;
        if(context instanceof PlaylistDetailFragment.PlaylistDetailListener){
            listener = (PlaylistDetailFragment.PlaylistDetailListener) context;
        }
    }

    @NonNull
    @Override
    public PlaylistDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = null;
        if(parent.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_detail_list_item, parent, false);
        }else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_detail_grid_item, parent, false);
        }

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistDetailAdapter.ViewHolder holder, int position) {
        holder.playlistDetailSongNameTextView.setText(tracks[position].getTitle());

        holder.playlistDetailPlayImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // we start the intent in the base activity
                listener.onPlaylistDetailClick(tracks, holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.length;
    }

    public void setTracks(AudioTrack[] tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistDetailSongNameTextView;
        ImageButton playlistDetailPlayImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            playlistDetailSongNameTextView = itemView.findViewById(R.id.playlistDetailSongNameTextView);
            playlistDetailPlayImageButton = itemView.findViewById(R.id.playlistDetailPlayImageButton);
        }
    }
}
