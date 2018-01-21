package com.example.shrawankeshari.playmusic.OfflineMusic;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by shrawankeshari on 23/12/17.
 */

public class MusicField implements Parcelable {

    private String song_title;
    private byte[] song_image;
    private String song_path;
    private String song_artist;
    private String song_duration;
    private int whenNoImage;

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_image(byte[] song_image) {
        this.song_image = song_image;
    }

    public byte[] getSong_image() {
        return song_image;
    }

    public void setSong_path(String song_path) {
        this.song_path = song_path;
    }

    public String getSong_path() {
        return song_path;
    }

    public void setSong_artist(String song_artist) {
        this.song_artist = song_artist;
    }

    public String getSong_artist() {
        return song_artist;
    }

    public void setSong_duration(String song_duration) {
        this.song_duration = song_duration;
    }

    public String getSong_duration() {
        return song_duration;
    }

    public void setWhenNoImage(int whenNoImage) {
        this.whenNoImage = whenNoImage;
    }

    public int getWhenNoImage() {
        return whenNoImage;
    }

    public MusicField() {

    }

    public MusicField(Parcel parcel) {
        Log.i("MUSIC", "Parcel Constructor");

        song_title = parcel.readString();
        song_image = parcel.createByteArray();
        song_path = parcel.readString();
        song_artist = parcel.readString();
        song_duration = parcel.readString();
        whenNoImage = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.i("MUSIC", "writeToParcel");

        parcel.writeString(song_title);
        parcel.writeByteArray(song_image);
        parcel.writeString(song_path);
        parcel.writeString(song_artist);
        parcel.writeString(song_duration);
        parcel.writeInt(whenNoImage);
    }

    public static final Parcelable.Creator<MusicField> CREATOR = new Parcelable.Creator<MusicField>() {

        @Override
        public MusicField createFromParcel(Parcel parcel) {
            Log.i("MUSIC", "createFromParcel");
            return new MusicField(parcel);
        }

        @Override
        public MusicField[] newArray(int i) {
            Log.i("MUSIC", "newArray");
            return new MusicField[i];
        }
    };
}
