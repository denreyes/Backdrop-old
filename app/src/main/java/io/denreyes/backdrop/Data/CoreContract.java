package io.denreyes.backdrop.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dj on 9/25/2015.
 */
public class CoreContract {
    public static final String CONTENT_AUTHORITY = "io.denreyes.backdrop.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_TRACKS = "tracks";

    public static final class TracksEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKS;

        public static final String TABLE_NAME = "tracks";
        public static final String TRACK_TITLE = "title";
        public static final String TRACK_ARTIST = "artist";
        public static final String TRACK_IMG_URL = "img";
        public static final String TRACK_SPOTIFY_ID = "spot_id";

        public static Uri buildTracksUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
