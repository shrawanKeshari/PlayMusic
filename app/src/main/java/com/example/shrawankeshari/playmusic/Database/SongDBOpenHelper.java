package com.example.shrawankeshari.playmusic.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shrawankeshari on 17/12/17.
 * SQLite openhelper class form creating the schema of database
 */

public class SongDBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PlayMusic.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MUSIC = "playMusic";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TIME = "time";


    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_MUSIC + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_URL + " TEXT, " +
            COLUMN_ARTIST + " TEXT, " +
            COLUMN_IMAGE + " TEXT, " +
            COLUMN_TIME + " TEXT " +
            ")";

    public SongDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);
        onCreate(db);
    }
}
