package io.denreyes.backdrop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.PlayConfig;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.Playlist.PlaylistAdapter;
import io.denreyes.backdrop.Playlist.PlaylistModel;
import io.denreyes.backdrop.Playlist.TracksContract;
import io.denreyes.backdrop.Playlist.TracksDBHelper;

/**
 * Created by Dj on 9/23/2015.
 */
public class MinimizedControllerFragment extends Fragment {
    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.text_artist)
    TextView mTextArtist;
    @Bind(R.id.img_album_art)
    SimpleDraweeView mImgArt;
    private boolean mBroadcastIsRegistered;
    private SharedPreferences prefPlayedPos, prefToken;
    private Player mPlayer;
    private int pos;
    ArrayList<PlaylistModel> model;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.controller_min, container, false);
        ButterKnife.bind(this, rootView);
        prefPlayedPos = getActivity().getSharedPreferences("PLAYED_POS_PREF", getActivity().MODE_PRIVATE);
        prefToken = getActivity().getSharedPreferences("ACCESS_TOKEN_PREF", getActivity().MODE_PRIVATE);
        pos = prefPlayedPos.getInt("PLAYED_POS", -1);
        if (pos != -1) {
            populateFromDb(pos);
        }

        Config playerConfig = new Config(getActivity(), prefToken.getString("ACCESS_TOKEN", ""),
                getActivity().getString(R.string.spotify_client_id));
        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
            @Override
            public void onInitialized(Player player) {
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
            }
        });

        return rootView;
    }

    private void populateFromDb(int pos) {
        SQLiteDatabase db = new TracksDBHelper(getActivity()).getWritableDatabase();
        Cursor cursor = db.query(
                TracksContract.TracksEntry.TABLE_NAME,
                null, null, null, null, null, null, null
        );
        cursor.move(pos);

        mTextTitle.setText(cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_TITLE)));
        mTextArtist.setText(cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_ARTIST)));
        mImgArt.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_IMG_URL))));
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBroadcastIsRegistered) {
            getActivity().unregisterReceiver(tracksReceiver);
            mBroadcastIsRegistered = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mBroadcastIsRegistered) {
            getActivity().registerReceiver(tracksReceiver,
                    new IntentFilter(PlaylistAdapter.BROADCAST_PLAYLIST_DATA));
            mBroadcastIsRegistered = true;
        }
    }

    private BroadcastReceiver tracksReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            model = intent.getParcelableArrayListExtra("LIST_TRACKS");
            int newPos = intent.getIntExtra("TRACK_POSITION", -1);
            PlayConfig config = PlayConfig.createFor(intent.getStringArrayListExtra("LIST_SONGS"));
            config.withTrackIndex(newPos);
            mPlayer.play(config);
            mPlayer.addPlayerNotificationCallback(new PlayerNotificationCallback() {
                @Override
                public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
                    if (eventType == EventType.TRACK_CHANGED) {
                        populateFromDb(getTrackPosition(playerState.trackUri, model) + 1);
                    }
                }

                @Override
                public void onPlaybackError(ErrorType errorType, String s) {
                    Toast.makeText(getActivity(), "err", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private int getTrackPosition(String trackUri, ArrayList<PlaylistModel> list) {
        for (int x = 0; x < list.size(); x++) {
            if (("spotify:track:" + list.get(x).track_id).equals(trackUri)) {
                return x;
            }
        }
        return 0;
    }
}