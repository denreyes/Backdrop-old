package io.denreyes.backdrop.fragments;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.denreyes.backdrop.MainActivity;
import io.denreyes.backdrop.R;
import io.denreyes.backdrop.datum.CoreContract;
import io.denreyes.backdrop.datum.CoreDBHelper;

/**
 * Created by Dj on 9/23/2015.
 */
public class MinimizedControllerFragment extends Fragment{
    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.text_artist)
    TextView mTextArtist;
    @Bind(R.id.img_album_art)
    SimpleDraweeView mImgArt;
    @Bind(R.id.img_btn_control)
    ImageView mImgPausePlay;

    private OnPausePlay mCallback;

    private SharedPreferences prefPlayedPos, prefIsPlaying;

    private boolean mNextBroadcastIsRegistered, isPlaying;
    private int pos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.controller_min, container, false);
        ButterKnife.bind(this, rootView);
        initPreferences();
        initIsPlayingViews();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNextBroadcastIsRegistered) {
            getActivity().unregisterReceiver(tracksReceiver);
            mNextBroadcastIsRegistered = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mNextBroadcastIsRegistered) {
            getActivity().registerReceiver(tracksReceiver,
                    new IntentFilter(MainActivity.BROADCAST_NEXT_TRACK));
            mNextBroadcastIsRegistered = true;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (MainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private BroadcastReceiver tracksReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            populateFromDb(intent.getIntExtra("POSITION", -1));
            mImgPausePlay.setImageResource(R.drawable.ic_pause);
            isPlaying = prefIsPlaying.getBoolean("IS_PLAYING",false);
        }
    };

    @OnClick(R.id.img_btn_control)
    public void onPausePlayClicked(){
        mCallback.onPausePlay(isPlaying);
        if(isPlaying) {
            mImgPausePlay.setImageResource(R.drawable.ic_play);
            isPlaying = false;
        }
        else {
            mImgPausePlay.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
        }
    }

    private void initPreferences() {
        prefIsPlaying = getActivity().getSharedPreferences("IS_PLAYING_PREF", getActivity().MODE_PRIVATE);
        prefPlayedPos = getActivity().getSharedPreferences("PLAYED_POS_PREF", getActivity().MODE_PRIVATE);
    }

    private void initIsPlayingViews() {
        pos = prefPlayedPos.getInt("PLAYED_POS", -1);
        if(pos != -1){
            populateFromDb(pos);
        }
        if(prefIsPlaying.getBoolean("IS_PLAYING",false)) {
            mImgPausePlay.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
        }
    }

    private void populateFromDb(int pos) {
        SQLiteDatabase db = new CoreDBHelper(getActivity()).getWritableDatabase();
        Cursor cursor = db.query(
                io.denreyes.backdrop.datum.CoreContract.TracksEntry.TABLE_NAME,
                null, null, null, null, null, null, null
        );
        cursor.move(pos);

        mTextTitle.setText(cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_TITLE)));
        mTextArtist.setText(cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_ARTIST)));
        mImgArt.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_IMG_URL))));
    }

    public interface OnPausePlay{
        public void onPausePlay(boolean bool);
    }
}