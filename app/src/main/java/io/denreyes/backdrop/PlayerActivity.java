package io.denreyes.backdrop;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.model.PlaylistAdapter;
import io.denreyes.backdrop.model.PlaylistModel;

/**
 * Created by DJ on 8/29/2015.
 */
public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new PlayerFragment()).commit();
    }


    @Override
    /**
     * Bug found in some when toolbar is half-way collapsed and a touch is made on image (some phones only)
     */
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    public static class PlayerFragment extends Fragment {
        @Bind(R.id.toolbar)
        Toolbar mToolbar;
        @Bind(R.id.recycler_playlist)
        RecyclerView mRecyclerPlaylist;
        @Bind(R.id.img_album_art)
        SimpleDraweeView mImgAlbumArt;

        PlaylistAdapter mAdapter;

        protected RecyclerView.LayoutManager mLayoutManager;
        protected LayoutManagerType mLayoutManagerType;

        private enum LayoutManagerType {
            GRID_LAYOUT_MANAGER,
            LINEAR_LAYOUT_MANAGER
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Fresco.initialize(getContext());
            View rootView = inflater.inflate(R.layout.fragment_player, container, false);
            ButterKnife.bind(this, rootView);

            mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

            ((PlayerActivity) getActivity()).setSupportActionBar(mToolbar);
            ((PlayerActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((PlayerActivity) getActivity()).getSupportActionBar().setTitle("I'll be good");
            mImgAlbumArt.setImageURI(Uri.parse("http://images.8tracks.com/cover/i/000/425/834/tumblr_mfm73difC01qcfzeko1_500-7180.jpg?rect=0,0,527,527&q=98&fm=jpg&fit=max"));

            setRecyclerViewLayoutManager(mLayoutManagerType);
            doMock();

            return rootView;
        }

        public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
            int scrollPosition = 0;

            if (mRecyclerPlaylist.getLayoutManager() != null) {
                scrollPosition = ((LinearLayoutManager) mRecyclerPlaylist.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
            }

            mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

            mRecyclerPlaylist.setLayoutManager(mLayoutManager);
            mRecyclerPlaylist.scrollToPosition(scrollPosition);
        }

        private void doMock() {
            ArrayList<PlaylistModel> test = new ArrayList<PlaylistModel>();

            test.add(new PlaylistModel("Suddently I See", "KT Tunstall", "http://eil.com/images/main/KT-Tunstall-Suddenly-I-See-386372.jpg"));
            test.add(new PlaylistModel("I can't stop this feeling I've got", "Razorlight", "https://upload.wikimedia.org/wikipedia/en/4/4b/I_Can't_Stop_This_Feeling_I've_Got.jpg"));
            test.add(new PlaylistModel("Put me in the car", "Ryan Gosling", "http://i.ytimg.com/vi/1cSSxhUTtVc/0.jpg"));
            test.add(new PlaylistModel("Pursuit of happiness (Kid Cudi cover)", "Lissie", "http://gloveboxx.com/wp2/wp-content/uploads/2012/07/Lissie.jpeg"));
            test.add(new PlaylistModel("Closer (live on KEXP)", "Tegan and Sara", "http://i.ytimg.com/vi/4T1s-ovpzMY/maxresdefault.jpg"));
            test.add(new PlaylistModel("Home", "Edward Sharpe & The Magnetic Zeros", "http://i.ytimg.com/vi/Fmtmgxk2J1g/maxresdefault.jpg"));
            test.add(new PlaylistModel("Tongue Tied", "Grouplove", "https://upload.wikimedia.org/wikipedia/en/thumb/f/f9/GrouploveTongueTied.jpg/220px-GrouploveTongueTied.jpg"));
            test.add(new PlaylistModel("What condition am I in", "Miles Kane", "http://ecx.images-amazon.com/images/I/51fcCBcoePL._SL500_AA280_.jpg"));

            mAdapter = new PlaylistAdapter(test);
            mRecyclerPlaylist.setAdapter(mAdapter);
        }
    }
}
