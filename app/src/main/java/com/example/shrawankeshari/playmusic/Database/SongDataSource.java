package com.example.shrawankeshari.playmusic.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shrawankeshari.playmusic.OfflineMusic.MusicField;

public class SongDataSource {

    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    //creating an array for holding the columns of the table
    private static final String[] allColumns = {
            SongDBOpenHelper.COLUMN_ID,
            SongDBOpenHelper.COLUMN_TITLE,
            SongDBOpenHelper.COLUMN_ARTIST,
            SongDBOpenHelper.COLUMN_DURATION,
            SongDBOpenHelper.COLUMN_PATH,
            SongDBOpenHelper.COLUMN_IMAGE,
            SongDBOpenHelper.COLUMN_WHEN_NO_IMAGE,
            SongDBOpenHelper.COLUMN_CREATE_TIME_STAMP
    };

    public SongDataSource(Context context){
        dbHelper = new SongDBOpenHelper(context);
    }

    //open database connection
    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    //close database connection
    public void close(){
        dbHelper.close();
    }

    //inserting the entries in the table
    public MusicField create(MusicField musicField){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SongDBOpenHelper.COLUMN_TITLE, musicField.getSong_title());
        contentValues.put(SongDBOpenHelper.COLUMN_ARTIST, musicField.getSong_artist());
        contentValues.put(SongDBOpenHelper.COLUMN_DURATION, musicField.getSong_duration());
        contentValues.put(SongDBOpenHelper.COLUMN_PATH, musicField.getSong_path());
        contentValues.put(SongDBOpenHelper.COLUMN_IMAGE, musicField.getSong_image());
        contentValues.put(SongDBOpenHelper.COLUMN_WHEN_NO_IMAGE, musicField.getWhenNoImage());
        contentValues.put(SongDBOpenHelper.COLUMN_CREATE_TIME_STAMP, musicField.getCreate_timeStamp());

        long insertId = database.insert(SongDBOpenHelper.TABLE_MUSIC, null, contentValues);
        musicField.setSong_id(insertId);

        return musicField;
    }


}
