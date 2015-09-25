package io.denreyes.backdrop.Playlist;

import android.provider.BaseColumns;

/**
 * Created by Dj on 9/25/2015.
 */
public class TracksContract {

    public final class TracksEntry implements BaseColumns {

        public static final String TABLE_NAME = "tracks";
        public static final String TRACK_TITLE = "title";
        public static final String TRACK_ARTIST = "artist";
        public static final String TRACK_IMG_URL = "img";
        public static final String TRACK_SPOTIFY_ID = "spot_id";
    }
}
