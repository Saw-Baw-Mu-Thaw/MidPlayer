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

public class GenreAdapter extends ArrayAdapter {

    List<String> genres;
    Context myContext;
    GenreFragment.GenreFragListener listener;

    public GenreAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        genres = objects;
        myContext = context;
        if(context instanceof GenreFragment.GenreFragListener) {
            listener = (GenreFragment.GenreFragListener) context;
        }
    }

    @Override
    public int getCount() {
        return genres.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(myContext);
            convertView = inflater.inflate(R.layout.genre_item, parent, false);
        }

        TextView genreItemNameTextView = convertView.findViewById(R.id.genreItemNameTextView);

        genreItemNameTextView.setText(genres.get(position));

        LinearLayout genreItem = convertView.findViewById(R.id.genreItem);
        genreItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGenreClick(genres.get(position));
            }
        });
        return convertView;
    }
}
