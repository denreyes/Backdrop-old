package io.denreyes.backdrop.Playlist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.PlayConfig;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.sdk.android.player.Spotify;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.PlayerFragment;
import io.denreyes.backdrop.R;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;

/**
 * Created by DJ on 8/29/2015.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private static final String LOG_TAG = PlaylistAdapter.class.getSimpleName();
    public static final String BROADCAST_PLAYLIST_DATA = "io.denreyes.backdrop.playlistdata";
    ArrayList<PlaylistModel> mList;
    String playlistId;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        Context context;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_artist)
        TextView mTextArtist;
        @Bind(R.id.img_album_art)
        SimpleDraweeView mImgAlbumArt;

        private SharedPreferences prefPlaylist, prefPlayedPos;
        private ArrayList<String> songs;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);
            prefPlayedPos = context.getSharedPreferences("PLAYED_POS_PREF", context.MODE_PRIVATE);
            prefPlaylist = context.getSharedPreferences("PLAYLIST_PREF", context.MODE_PRIVATE);

            songs = new ArrayList<>();
        }

        @Override
        public void onClick(View v) {
            updateController(mList);

            //New Playlist Played
            if (!prefPlaylist.getString("PLAYLIST_ID", "").equals(playlistId))
                playlistDbUpdate();
        }

        private void playlistDbUpdate() {
            prefPlaylist.edit().putString("PLAYLIST_ID", playlistId).apply();
            SQLiteDatabase db = new TracksDBHelper(context).getWritableDatabase();
            db.delete(TracksContract.TracksEntry.TABLE_NAME, null, null);
            ContentValues values = new ContentValues();
            for (int x = 0; x < getItemCount(); x++) {
                values.put(TracksContract.TracksEntry.TRACK_TITLE, mList.get(x).title);
                values.put(TracksContract.TracksEntry.TRACK_ARTIST, mList.get(x).artist);
                values.put(TracksContract.TracksEntry.TRACK_IMG_URL, mList.get(x).img_url);
                values.put(TracksContract.TracksEntry.TRACK_SPOTIFY_ID, mList.get(x).track_id);

                db.insert(TracksContract.TracksEntry.TABLE_NAME, null, values);
            }
        }

        public void updateController(ArrayList<PlaylistModel> list) {
            prefPlayedPos.edit().putInt("PLAYED_POS",getPosition()+1).apply();
            for (int x=0;x<mList.size();x++){
                songs.add("spotify:track:" + mList.get(x).track_id);
            }

            Intent intent = new Intent(BROADCAST_PLAYLIST_DATA);
            intent.putParcelableArrayListExtra("LIST_TRACKS", list);
            intent.putExtra("TRACK_POSITION", getPosition());
            intent.putStringArrayListExtra("LIST_SONGS", songs);
            context.sendBroadcast(intent);
        }
    }

    public PlaylistAdapter(ArrayList<PlaylistModel> list, String playlistId) {
        mList = list;
        this.playlistId = playlistId;
    }

    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_playlist, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        holder.mTextTitle.setText(mList.get(position).title);
        holder.mTextArtist.setText("by " + mList.get(position).artist);
        holder.mImgAlbumArt.setImageURI(Uri.parse(mList.get(position).img_url));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
