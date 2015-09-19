package io.denreyes.backdrop.Playlist;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.R;

/**
 * Created by DJ on 8/29/2015.
 */
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private static final String LOG_TAG = PlaylistAdapter.class.getSimpleName();
    ArrayList<PlaylistModel> mList;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        Context context;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_artist)
        TextView mTextArtist;
        @Bind(R.id.img_album_art)
        SimpleDraweeView mImgAlbumArt;

        private Player mPlayer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
            itemView.setOnClickListener(this);

            SharedPreferences prefToken = context.getSharedPreferences("ACCESS_TOKEN_PREF", context.MODE_PRIVATE);
            Config playerConfig = new Config(context, prefToken.getString("ACCESS_TOKEN",""),
                    context.getString(R.string.spotify_client_id));

            mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized(Player player) {
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        }

        @Override
        public void onClick(View v) {
            mPlayer.play("spotify:track:" + mList.get(getPosition()).track_id);
        }
    }

    public PlaylistAdapter(ArrayList<PlaylistModel> list) {
        mList = list;

        Log.v("HEYY", list.size() + "");
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
        Log.v("HEYY", position + ", " + holder.mTextTitle);
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
