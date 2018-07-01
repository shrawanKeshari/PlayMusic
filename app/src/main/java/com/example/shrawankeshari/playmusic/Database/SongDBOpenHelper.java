package com.example.shrawankeshari.playmusic.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SongDBOpenHelper extends SQLiteOpenHelper {

    //database name
    private static final String DATABASE_NAME = "PlayMusic.db";

    //set the database version, if it is 1 means its initially created and if greater than 1 means
    //database is updated
    private static final int DATABASE_VERSION = 1;

    //creating field
    public static final String TABLE_MUSIC = "playMusic";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ARTIST = "artist";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_WHEN_NO_IMAGE = "whenNoImage";
    public static final String COLUMN_CREATE_TIME_STAMP = "create_timeStamp";

    //statement for creating the table playMusic in the database
    private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_MUSIC + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_ARTIST + " TEXT, " +
            COLUMN_DURATION + " TEXT, " +
            COLUMN_PATH + " TEXT, " +
            COLUMN_IMAGE + " BLOB, " +
            COLUMN_WHEN_NO_IMAGE + " INTEGER, " +
            COLUMN_CREATE_TIME_STAMP + " TEXT " +
            ")";

    //field for table favorite
    public static final String TABLE_FAVORITE = "favorite";

    //statement for creating the table favorite in the database
    private static final String TABLE_CREATE_FAVORITE = "CREATE TABLE " + TABLE_FAVORITE + " (" +
            COLUMN_ID + "INTEGER PRIMARY KEY)";

    //field for table history
    public static final String TABLE_HISTORY = "history";

    //statement for creating the table history in the database
    private static final String TABLE_CREATE_HISTORY = "CREATE TABLE " + TABLE_HISTORY + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_CREATE_TIME_STAMP + " TEXT" + ")";

    //creating default constructor
    public SongDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //creating playMusic table
        sqLiteDatabase.execSQL(TABLE_CREATE);

        //creating favorite table
        sqLiteDatabase.execSQL(TABLE_CREATE_FAVORITE);

        //creating history table
        sqLiteDatabase.execSQL(TABLE_CREATE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MUSIC);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(sqLiteDatabase);
    }
}
