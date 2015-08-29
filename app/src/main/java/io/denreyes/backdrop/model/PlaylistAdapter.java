package io.denreyes.backdrop.model;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

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

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        Context context;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_artist)
        TextView mTextArtist;
        @Bind(R.id.img_album_art)
        SimpleDraweeView mImgAlbumArt;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        @Override
        public void onClick(View v) {

        }
    }

    public PlaylistAdapter(ArrayList<PlaylistModel> list) {
        mList = list;

        Log.v("HEYY",list.size()+"");
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
        Log.v("HEYY", position+", "+holder.mTextTitle);
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
