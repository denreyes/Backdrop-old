package io.denreyes.backdrop;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.Playlist.TracksContract;
import io.denreyes.backdrop.Playlist.TracksDBHelper;

/**
 * Created by Dj on 9/23/2015.
 */
public class MaximizedControllerFragment extends Fragment {
    @Bind(R.id.text_title)
    TextView mTextTitle;
    @Bind(R.id.text_artist)
    TextView mTextArtist;
    @Bind(R.id.text_next_title)
    TextView mTextNextTitle;
    @Bind(R.id.img_album_art)
    SimpleDraweeView mImgArt;
    private SharedPreferences prefPlayedPos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.controller_max,container,false);
        ButterKnife.bind(this, rootView);
        prefPlayedPos = getActivity().getSharedPreferences("PLAYED_POS_PREF", getActivity().MODE_PRIVATE);
        int pos = prefPlayedPos.getInt("PLAYED_POS", -1);
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
        cursor.moveToNext();
        mTextNextTitle.setText(cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_TITLE)));
    }
}
