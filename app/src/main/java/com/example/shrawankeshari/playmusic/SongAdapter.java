package com.example.shrawankeshari.playmusic;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by shrawankeshari on 16/12/17.
 * Adapter class used to populating the data to list view
 */
public class SongAdapter extends ArrayAdapter<SongsField> {

    Context context;
    List<SongsField> songsList;

    public SongAdapter(Context context, int resource, List<SongsField> placeList) {
        super(context, resource, placeList);
        this.context = context;
        this.songsList = placeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.item_list, parent, false);
        }

        //initializing the references to the vews
        ImageView im = (ImageView) convertView.findViewById(R.id.list_image);
        TextView tv_name = (TextView) convertView.findViewById(R.id.song_name);
        TextView tv_artist = (TextView) convertView.findViewById(R.id.artist_name);

        SongsField songsField = songsList.get(position);

        //displaying the data from web
        tv_name.setText(songsField.getSong_name());
        tv_artist.setText(songsField.getSong_artists());

        Glide.with(im.getContext())
                .load(songsField.getSong_image())
                .into(im);

        return convertView;
    }
}
