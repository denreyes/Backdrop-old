package io.denreyes.backdrop.datum;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


/**
 * Created by Dj on 10/3/2015.
 */
public class CoreProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private CoreDBHelper mOpenHelper;

    static final int SHOWCASE = 100;
    static final int TRACKS = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CoreContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, CoreContract.PATH_TRACKS, TRACKS);
        matcher.addURI(authority, CoreContract.PATH_SHOWCASE, SHOWCASE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CoreDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TRACKS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CoreContract.TracksEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SHOWCASE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CoreContract.ShowcaseEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case TRACKS:
                return CoreContract.TracksEntry.CONTENT_TYPE;
            case SHOWCASE:
                return CoreContract.ShowcaseEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TRACKS: {
                long _id = db.insert(CoreContract.TracksEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CoreContract.TracksEntry.buildTracksUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SHOWCASE: {
                long _id = db.insert(CoreContract.ShowcaseEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CoreContract.ShowcaseEntry.buildTracksUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case TRACKS: {
                rowsDeleted = db.delete(
                        CoreContract.TracksEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case SHOWCASE: {
                rowsDeleted = db.delete(
                        CoreContract.ShowcaseEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case TRACKS:
                rowsUpdated = db.update(CoreContract.TracksEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SHOWCASE:
                rowsUpdated = db.update(CoreContract.ShowcaseEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
