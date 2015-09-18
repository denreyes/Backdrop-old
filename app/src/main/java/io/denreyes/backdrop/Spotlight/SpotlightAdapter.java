package io.denreyes.backdrop.Spotlight;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import io.denreyes.backdrop.PlayerActivity;
import io.denreyes.backdrop.R;

/**
 * Created by DJ on 8/28/2015.
 */
public class SpotlightAdapter extends RecyclerView.Adapter<SpotlightAdapter.ViewHolder> {
    private static final String LOG_TAG = SpotlightAdapter.class.getSimpleName();
    private static ArrayList<SpotlightModel> mList;

    public static class ViewHolder extends RecyclerView.ViewHolder
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

            infoBackground.setAlpha(0.70f);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("PLAYLIST_ID",mList.get(getPosition()).id);
            intent.putExtra("PLAYLIST_IMG",mList.get(getPosition()).img_url);
            intent.putExtra("PLAYLIST_TITLE",mList.get(getPosition()).title);
            intent.putExtra("PLAYLIST_MIXER",mList.get(getPosition()).mixer);
            context.startActivity(intent);
        }
    }

    public SpotlightAdapter(ArrayList<SpotlightModel> list) {
        mList = list;
    }

    @Override
    public SpotlightAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Fresco.initialize(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_spotlight, parent, false);
        view.setFocusable(true);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SpotlightAdapter.ViewHolder holder, int position) {
        Log.d(LOG_TAG, "Element " + position + " set.");

        holder.mTextTitle.setText(mList.get(position).title);
        holder.mTextMixer.setText("by " + mList.get(position).mixer);
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
