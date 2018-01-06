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

    //method for parsing the json data we get in the string
    public static List<SongsField> parse(String data) {
        try {
            //converting the string into proper json data
            JSONArray array = new JSONArray(data);
            //initializing the list object for holding the data from the json parsing
            List<SongsField> songList = new ArrayList<>();

            //getting number of element in the json array
            int length = array.length();

            //iterating over the json array
            for (int i = 0; i < length; i++) {
                //getting the current json object
                JSONObject obj = array.getJSONObject(i);

                //creating SongsField object
                SongsField field = new SongsField();

                //creating the SongsField data
                field.setSong_name(obj.getString("song"));
                field.setSong_url(obj.getString("url"));
                field.setSong_artists(obj.getString("artists"));
                field.setSong_image(obj.getString("cover_image"));
                field.setSong_time("");

                //putting the SongsField data into the list
                songList.add(field);
            }

            //returning the list os songs
            return songList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
