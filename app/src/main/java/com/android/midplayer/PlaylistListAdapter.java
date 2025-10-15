package com.android.midplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistListAdapter extends ArrayAdapter<Playlist> {

    private PlaylistFragment.PlaylistFragListener listener; // same as context
    private Context mainActContext;
    private List<Playlist> playlists;

    private boolean is_portrait;

    public PlaylistListAdapter(@NonNull Context context, int resource, @NonNull List<Playlist> objects) {
        super(context, resource, objects);
        listener = (PlaylistFragment.PlaylistFragListener) context;
        mainActContext = context;
        playlists = objects;

    }

    @Override
    public int getCount() {
        return playlists.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        this.is_portrait = mainActContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mainActContext);

            if(is_portrait){
                convertView =inflater.inflate(R.layout.playlist_list_item, parent, false);
            }else{
                convertView =inflater.inflate(R.layout.playlist_grid_item, parent, false);
            }
        }




        View view = convertView;
        TextView playlistNameTextView = view.findViewById(R.id.playlistItemNameTextView);
        TextView playlistSongCountTextView = view.findViewById(R.id.playlistItemSongCountTextView);
        ImageButton playlistMoreButton = view.findViewById(R.id.playlistItemMoreButton);

        playlistNameTextView.setText(playlists.get(position).getName());

        playlistSongCountTextView.setText("Songs: "+Integer.toString(playlists.get(position).getNumber_of_songs()));

        playlistMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mainActContext, view);

                popup.getMenuInflater().inflate(R.menu.playlist_item_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        if (itemId == R.id.menu_item_rename) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mainActContext);
                            builder.setTitle("Rename Playlist");

                            EditText playlistRenameEditText = new EditText(mainActContext);
                            playlistRenameEditText.setText(playlists.get(position).getName());

                            builder.setView(playlistRenameEditText);

                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String new_name = playlistRenameEditText.getText().toString();
                                    playlists.get(position).setName(new_name);
                                    listener.onPlaylistRenamed(playlists);
                                    notifyDataSetChanged();
                                    // tell base activity to also rename playlist in file
                                }
                            });

                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //  do nothing
                                }
                            });

                            builder.create().show();
                        } else if (itemId == R.id.menu_item_delete) {
                            // delete playlist
                            listener.onPlaylistRemove(position);
                            notifyDataSetChanged();
                            Toast.makeText(mainActContext, "Playlist Deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            // play all songs in playlist
                            if (listener != null) {
                                listener.onPlayAllSongs(playlists.get(position));
                            }
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });

        LinearLayout playlistItem = view.findViewById(R.id.playlistItem);
        playlistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPlaylistClick(playlists.get(position));
            }
        });

        if(!is_portrait){
            ImageView playlistItemLogo = view.findViewById(R.id.playlistItemLogo);
            playlistItemLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPlaylistClick(playlists.get(position));
                }
            });
        }


        return convertView;
    }
}
