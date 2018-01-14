package com.example.shrawankeshari.playmusic.OnlineMusic;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shrawankeshari.playmusic.R;

import java.util.List;

/**
 * Created by shrawankeshari on 16/12/17.
 * Adapter class used to populating the data to list view
 */
public class SongAdapter extends ArrayAdapter<SongsField> {

    //context object for getting the context of the activity
    Context context;

    //list object for holding the song list
    List<SongsField> songsList;

    public SongAdapter(Context context, int resource, List<SongsField> songList) {
        super(context, resource, songList);
        this.context = context;
        this.songsList = songList;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.item_list_online, parent, false);
        }

        //initializing the references to the vews
        ImageView im = (ImageView) convertView.findViewById(R.id.list_image_online);
        TextView tv_name = (TextView) convertView.findViewById(R.id.song_name_online);
        TextView tv_artist = (TextView) convertView.findViewById(R.id.artist_name_online);

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
