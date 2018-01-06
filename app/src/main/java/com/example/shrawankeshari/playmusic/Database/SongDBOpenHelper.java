package com.example.shrawankeshari.playmusic.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shrawankeshari on 17/12/17.
 * SQLite openhelper class form creating the schema of database
 */

public class SongDBOpenHelper extends SQLiteOpenHelper {

    //create the database
    private static final String DATABASE_NAME = "PlayMusic.db";

    //set the database version, if it is 1 means its initially created and if greater than 1 means
    // database is updated
    private static final int DATABASE_VERSION = 4;

    //creating the field for table in the database
    public static final String TABLE_MUSIC = "playMusic";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TIME = "time";


    //statement for creating the table playMusic in the database
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_MUSIC + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_URL + " TEXT, " +
            COLUMN_ARTIST + " TEXT, " +
            COLUMN_IMAGE + " TEXT, " +
            COLUMN_TIME + " TEXT " +
            ")";

    //field for table favorite
    public static final String TABLE_FAVORITE = "favorite";

    //statement for creating the table favorite in the database
    private static final String TABLE_CREATE_FAVORITE = "CREATE TABLE " + TABLE_FAVORITE + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY)";

    //field for table history
    public static final String TABLE_HISTORY = "history";

    //statement for creating the table history in the database
    private static final String TABLE_CREATE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_TIME + " TEXT " +
            ")";

    //constructor
    public SongDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //create the table in the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating table playMusic
        db.execSQL(TABLE_CREATE);

        //creating table favorite
        db.execSQL(TABLE_CREATE_FAVORITE);

        //creating table history
        db.execSQL(TABLE_CREATE_HISTORY);
    }

    //update the table in the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }
}
