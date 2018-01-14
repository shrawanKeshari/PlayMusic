package com.example.shrawankeshari.playmusic.OnlineMusic;

/**
 * Created by shrawankeshari on 16/12/17.
 * class used to define the fields of the song API
 */

public class SongsField {

    private long songId;
    private String song_name;
    private String song_url;
    private String song_artists;
    private String song_image;
    private String song_time;

    public void setSongId(long id) {
        songId = id;
    }

    public long getSongId() {
        return songId;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public String getSong_url() {
        return song_url;
    }

    public void setSong_artists(String song_artists) {
        this.song_artists = song_artists;
    }

    public String getSong_artists() {
        return song_artists;
    }

    public void setSong_image(String song_image) {
        this.song_image = song_image;
    }

    public String getSong_image() {
        return song_image;
    }

    public void setSong_time(String song_time) {
        this.song_time = song_time;
    }

    public String getSong_time() {
        return song_time;
    }
}
