package io.denreyes.backdrop.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.MainActivity;
import io.denreyes.backdrop.datum.CoreContract;
import io.denreyes.backdrop.model.ShowcaseModel;
import io.denreyes.backdrop.fragments.PlaylistFragment;
import io.denreyes.backdrop.R;

/**
 * Created by DJ on 8/28/2015.
 */
public class ShowcaseAdapter extends RecyclerView.Adapter<ShowcaseAdapter.ViewHolder> {
    private static final String LOG_TAG = ShowcaseAdapter.class.getSimpleName();
    private static ArrayList<ShowcaseModel> mList;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        Context context;
        @Bind(R.id.text_title)
        TextView mTextTitle;
        @Bind(R.id.text_mixer)
        TextView mTextMixer;
        @Bind(R.id.img_thumbnail)
        SimpleDraweeView mImgThumbnail;
        @Bind(R.id.info_bg)
        ImageView infoBackground;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();

//            infoBackground.setAlpha(0.70f);
            infoBackground.setVisibility(View.GONE);
            mTextTitle.setVisibility(View.GONE);
            mTextMixer.setVisibility(View.GONE);

            showcaseDbUpdate();
        }

        @Override
        public void onClick(View v) {
            switchToPlaylistFragment();
        }


        private void showcaseDbUpdate() {
            new Thread() {
                public void run() {
                    ContentResolver contentResolver = context.getContentResolver();
                    contentResolver.delete(CoreContract.ShowcaseEntry.CONTENT_URI, null, null);
                    ContentValues values = new ContentValues();
                    for (int x = 0; x < getItemCount(); x++) {
                        values.put(CoreContract.ShowcaseEntry.PLAYLIST_TITLE, mList.get(x).title);
                        values.put(CoreContract.ShowcaseEntry.PLAYLIST_MIXER, mList.get(x).mixer);
                        values.put(CoreContract.ShowcaseEntry.PLAYLIST_IMG_URL, mList.get(x).img_url);
                        values.put(CoreContract.ShowcaseEntry.PLAYLIST_SPOTIFY_ID, mList.get(x).id);

                        contentResolver.insert(CoreContract.ShowcaseEntry.CONTENT_URI,values);
                    }
                }
            }.start();
        }

        private void switchToPlaylistFragment() {
            Bundle bundle = new Bundle();
            bundle.putString("PLAYLIST_ID", mList.get(getPosition()).id);
            bundle.putString("PLAYLIST_IMG", mList.get(getPosition()).img_url);
            bundle.putString("PLAYLIST_TITLE", mList.get(getPosition()).title);
            bundle.putString("PLAYLIST_MIXER", mList.get(getPosition()).mixer);

            PlaylistFragment playlistFragment = new PlaylistFragment();
            playlistFragment.setArguments(bundle);
            FragmentTransaction ft = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
            ft.addToBackStack(playlistFragment.getClass().getName());
            ft.replace(R.id.container, playlistFragment).commit();
        }
    }

    public ShowcaseAdapter(ArrayList<ShowcaseModel> list) {
        mList = list;
    }

    @Override
    public ShowcaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_spotlight, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ShowcaseAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

//        holder.mTextTitle.setText(mList.get(position).title);
//        holder.mTextMixer.setText("by " + mList.get(position).mixer);
        holder.mImgThumbnail.setImageURI(Uri.parse(mList.get(position).img_url));
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
