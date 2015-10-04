package io.denreyes.backdrop.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Dj on 10/3/2015.
 */
public class CoreProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CoreDBHelper mOpenHelper;

    static final int TRACKS = 100;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
//        final String authority = CoreContract.CONTENT_AUTHORITY;
//
//        matcher.addURI(authority, CoreContract.PATH_TRACKS, TRACKS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
