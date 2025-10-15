package com.android.midplayer;

public interface OnPlaylistClickListener {
    public void onPlaylistClick(Playlist playlist);
    public void onPlaylistRemove(Playlist playlist);
    public void onPlayAllSongs(Playlist playlist);
}
