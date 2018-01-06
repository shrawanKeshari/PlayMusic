package com.example.shrawankeshari.playmusic.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.shrawankeshari.playmusic.SongsField;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrawankeshari on 17/12/17.
 * class for inserting and fetching data from database
 */

public class SongDataSource {
    SQLiteOpenHelper dbHelper;
    SQLiteDatabase database;

    //creating an array for holding the columns of the table
    private static final String[] allColumns = {
            SongDBOpenHelper.COLUMN_ID,
            SongDBOpenHelper.COLUMN_NAME,
            SongDBOpenHelper.COLUMN_URL,
            SongDBOpenHelper.COLUMN_ARTIST,
            SongDBOpenHelper.COLUMN_IMAGE,
            SongDBOpenHelper.COLUMN_TIME
    };

    public SongDataSource(Context context) {
        dbHelper = new SongDBOpenHelper(context);
    }

    //open the databse
    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    //close the database after doing the work
    public void close() {
        dbHelper.close();
    }

    //inserting the entries in the table
    public SongsField create(SongsField songsField) {
        ContentValues values = new ContentValues();
        values.put(SongDBOpenHelper.COLUMN_NAME, songsField.getSong_name());
        values.put(SongDBOpenHelper.COLUMN_URL, songsField.getSong_url());
        values.put(SongDBOpenHelper.COLUMN_ARTIST, songsField.getSong_artists());
        values.put(SongDBOpenHelper.COLUMN_IMAGE, songsField.getSong_image());
        values.put(SongDBOpenHelper.COLUMN_TIME, songsField.getSong_time());
        long insertId = database.insert(SongDBOpenHelper.TABLE_MUSIC, null, values);
        songsField.setSongId(insertId);

        return songsField;
    }

    //fetching the data from the table
    public List<SongsField> findAll() {
        Cursor cursor = database.query(SongDBOpenHelper.TABLE_MUSIC, allColumns, null,
                null, null, null, null);

        List<SongsField> song = cursorToList(cursor);

        return song;
    }

    //fetching the filtered data from the table
    public List<SongsField> findFiltered(String selection) {
        Cursor cursor = database.query(SongDBOpenHelper.TABLE_MUSIC, allColumns, selection,
                null, null, null, null);

        List<SongsField> song = cursorToList(cursor);

        return song;
    }

    //converting the data obtain from table to the list, so that data can be populated on the listview
    private List<SongsField> cursorToList(Cursor cursor) {
        List<SongsField> songs = new ArrayList<SongsField>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                SongsField songsField = new SongsField();
                songsField.setSongId(cursor.getLong(cursor.getColumnIndex(SongDBOpenHelper.COLUMN_ID)));
                songsField.setSong_name(cursor.getString(cursor.getColumnIndex
                        (SongDBOpenHelper.COLUMN_NAME)));
                songsField.setSong_url(cursor.getString(cursor.getColumnIndex
                        (SongDBOpenHelper.COLUMN_URL)));
                songsField.setSong_artists(cursor.getString(cursor.getColumnIndex
                        (SongDBOpenHelper.COLUMN_ARTIST)));
                songsField.setSong_image(cursor.getString(cursor.getColumnIndex
                        (SongDBOpenHelper.COLUMN_IMAGE)));
                songsField.setSong_time(cursor.getString(cursor.getColumnIndex
                        (SongDBOpenHelper.COLUMN_TIME)));
                songs.add(songsField);
            }
        }

        return songs;
    }

    //add the current song to favorite table
    public boolean addToFavorite(SongsField songsField) {
        ContentValues values = new ContentValues();
        values.put(SongDBOpenHelper.COLUMN_ID, songsField.getSongId());

        long result = database.insert(SongDBOpenHelper.TABLE_FAVORITE, null, values);

        return (result != -1);
    }

    //remove the current song from the favorite table
    public boolean removeFavorite(SongsField songsField) {
        String where = SongDBOpenHelper.COLUMN_ID + "=" + songsField.getSongId();
        int result = database.delete(SongDBOpenHelper.TABLE_FAVORITE, where, null);
        return (result == 1);
    }

    //add the song to the history table with the updated time
    public void addToHistory(SongsField songsField,long timeStamp){
        ContentValues values = new ContentValues();
        values.put(SongDBOpenHelper.COLUMN_ID, songsField.getSongId());
        values.put(SongDBOpenHelper.COLUMN_TIME, String.valueOf(timeStamp));

        long result = database.insert(SongDBOpenHelper.TABLE_HISTORY, null, values);

    }

    //update the timeStamp of the song present in the table
    public void updateToHistory(SongsField songsField, long timeStamp){
        ContentValues values = new ContentValues();
        values.put(SongDBOpenHelper.COLUMN_ID, songsField.getSongId());
        values.put(SongDBOpenHelper.COLUMN_TIME, String.valueOf(timeStamp));

        long result = database.update(SongDBOpenHelper.TABLE_HISTORY, values,
                "id="+songsField.getSongId(),null);
    }

    //fetching the history of the user
    public List<SongsField> findHistory(){
        String query = "SELECT * FROM playMusic JOIN history ON " +
                "playMusic.id = history.id ORDER BY history.time DESC";

        Cursor cursor = database.rawQuery(query, null);

        List<SongsField> song = cursorToList(cursor);

        return song;
    }

    //fetching the favorite songs from the table
    public List<SongsField> findFavorite() {
        String query = "SELECT * FROM playMusic JOIN favorite ON " +
                "playMusic.id = favorite.id";

        Cursor cursor = database.rawQuery(query, null);

        List<SongsField> song = cursorToList(cursor);

        return song;
    }

    //checking if the song is in the favorite table or not
    public boolean findSongFavorite(long id) {
        String query = "SELECT * FROM " + SongDBOpenHelper.TABLE_FAVORITE + " WHERE " +
                SongDBOpenHelper.COLUMN_ID + "=" + "\"" + id + "\"";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            return false;
        }

        return true;
    }

    public boolean findInHistory(long id){
        String query = "SELECT * FROM " + SongDBOpenHelper.TABLE_HISTORY + " WHERE " +
                SongDBOpenHelper.COLUMN_ID + "=" + "\"" + id + "\"";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            return false;
        }

        return true;
    }
}
