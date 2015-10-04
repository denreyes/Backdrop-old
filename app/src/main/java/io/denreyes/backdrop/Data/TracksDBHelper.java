package io.denreyes.backdrop.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dj on 9/25/2015.
 */
public class TracksDBHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "tracks.db";

    public TracksDBHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION);}


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRACKS_TABLES = "CREATE TABLE " + TracksContract.TracksEntry.TABLE_NAME + " (" +
                TracksContract.TracksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TracksContract.TracksEntry.TRACK_TITLE + " TEXT NOT NULL, " +
                TracksContract.TracksEntry.TRACK_ARTIST + " TEXT NOT NULL, " +
                TracksContract.TracksEntry.TRACK_IMG_URL + " TEXT NOT NULL, " +
                TracksContract.TracksEntry.TRACK_SPOTIFY_ID + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_TRACKS_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TracksContract.TracksEntry.TABLE_NAME);
        onCreate(db);
    }
}
