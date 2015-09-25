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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

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
    private SharedPreferences prefPlayedPos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.controller_min,container,false);
        ButterKnife.bind(this,rootView);
        prefPlayedPos = getActivity().getSharedPreferences("PLAYED_POS_PREF", getActivity().MODE_PRIVATE);
        int pos = prefPlayedPos.getInt("PLAYED_POS",-1);
        if(pos != -1){
            populateFromDb(pos);
        }

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
            ArrayList<PlaylistModel> model = intent.getParcelableArrayListExtra("LIST_TRACKS");
            int position = intent.getIntExtra("TRACK_POSITION",-1);
            mTextTitle.setText(model.get(position).title);
            mTextArtist.setText(model.get(position).artist);
            mImgArt.setImageURI(Uri.parse(model.get(position).img_url));
        }
    };
}
