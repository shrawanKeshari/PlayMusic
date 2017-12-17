package com.example.shrawankeshari.playmusic.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

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

    public List<SongsField> findAll() {
        Cursor cursor = database.query(SongDBOpenHelper.TABLE_MUSIC, allColumns, null,
                null, null, null, null);

        List<SongsField> song = cursorToList(cursor);

        return song;
    }

    public List<SongsField> findFiltered(String selection) {
        Cursor cursor = database.query(SongDBOpenHelper.TABLE_MUSIC, allColumns, selection,
                null, null, null, null);

        List<SongsField> song = cursorToList(cursor);

        return song;
    }

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
}
