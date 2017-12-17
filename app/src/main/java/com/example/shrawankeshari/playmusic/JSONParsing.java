package com.example.shrawankeshari.playmusic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrawankeshari on 16/12/17.
 */

public class JSONParsing {

    public static List<SongsField> parse(String data) {
        try {
            JSONArray array = new JSONArray(data);
            List<SongsField> songList = new ArrayList<>();

            int length = array.length();

            for (int i = 0; i < length; i++) {
                JSONObject obj = array.getJSONObject(i);

                SongsField field = new SongsField();

                field.setSong_name(obj.getString("song"));
                field.setSong_url(obj.getString("url"));
                field.setSong_artists(obj.getString("artists"));
                field.setSong_image(obj.getString("cover_image"));
                field.setSong_time("");
                songList.add(field);
            }

            return songList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
