package com.android.midplayer;

import android.content.Context;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ArtistAdapter extends ArrayAdapter {
    private List<String> names;
    private Context myContext;
    private ArtistFragment.ArtistFragListener listener;

    public ArtistAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        myContext = context;
        names = objects;
        if (context instanceof ArtistFragment.ArtistFragListener) {
            listener = (ArtistFragment.ArtistFragListener) context;
        }
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(myContext);
            convertView = inflater.inflate(R.layout.artist_item, parent, false);
        }

        TextView artistItemNameTextView = convertView.findViewById(R.id.artistItemNameTextView);

        artistItemNameTextView.setText(names.get(position));

        LinearLayout artistItem = convertView.findViewById(R.id.artistItem);
        artistItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onArtistClick(names.get(position));
            }
        });
        return convertView;
    }
}
