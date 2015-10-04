package io.denreyes.backdrop.datum;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dj on 9/25/2015.
 */
public class CoreDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "tracks.db";

    public CoreDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION);}


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_SHOWCASE_TABLES = "CREATE TABLE " + CoreContract.ShowcaseEntry.TABLE_NAME + " (" +
                CoreContract.ShowcaseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CoreContract.ShowcaseEntry.PLAYLIST_TITLE + " TEXT NOT NULL, " +
                CoreContract.ShowcaseEntry.PLAYLIST_MIXER + " TEXT NOT NULL, " +
                CoreContract.ShowcaseEntry.PLAYLIST_IMG_URL + " TEXT NOT NULL, " +
                CoreContract.ShowcaseEntry.PLAYLIST_SPOTIFY_ID + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_TRACKS_TABLE = "CREATE TABLE " + CoreContract.TracksEntry.TABLE_NAME + " (" +
                CoreContract.TracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CoreContract.TracksEntry.TRACK_TITLE + " TEXT NOT NULL, " +
                CoreContract.TracksEntry.TRACK_ARTIST + " TEXT NOT NULL, " +
                CoreContract.TracksEntry.TRACK_IMG_URL + " TEXT NOT NULL, " +
                CoreContract.TracksEntry.TRACK_SPOTIFY_ID + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_SHOWCASE_TABLES);
        db.execSQL(SQL_CREATE_TRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CoreContract.TracksEntry.TABLE_NAME);
        onCreate(db);
    }
}
