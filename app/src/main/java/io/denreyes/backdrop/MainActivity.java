package io.denreyes.backdrop;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.denreyes.backdrop.Spotlight.SpotlightAdapter;
import io.denreyes.backdrop.Spotlight.SpotlightModel;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.dashboard_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        return super.onCreateOptionsMenu(menu);
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

    public static class MainFragment extends Fragment {
        @Bind(R.id.toolbar)
        Toolbar mToolbar;
        @Bind(R.id.recycler_spotlight)
        RecyclerView mRecyclerSpotlight;
        @Bind(R.id.backdrop)
        ImageView mImageBackdrop;

        private SpotlightAdapter mAdapter;
        private SpotifyApi mApi;
        private SpotifyService mSpotify;

        private final String LOG_TAG = MainFragment.class.getSimpleName();
        private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, rootView);
            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            mApi = new SpotifyApi();
            mApi = mApi.setAccessToken(getString(R.string.spotify_token));
            mSpotify = mApi.getService();

            mImageBackdrop.setImageResource(R.drawable.img_storm_white);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(getResources().getInteger(R.integer.list_column_count),
                            StaggeredGridLayoutManager.VERTICAL);
            mRecyclerSpotlight.setLayoutManager(sglm);

            fetchFeaturedPlaylists();
            return rootView;
        }

        @OnClick(R.id.fab_drop)
        public void onDropClick() {
            startActivity(new Intent(getActivity(), AmbientActivity.class));
        }

        private void fetchFeaturedPlaylists() {
            mSpotify.getFeaturedPlaylists(new Callback<FeaturedPlaylists>() {
                @Override
                public void success(FeaturedPlaylists featuredPlaylists, Response response) {
                    int playlistSize = featuredPlaylists.playlists.items.size();

                    if (playlistSize != 0) {
                        String spotifyId, playlistTitle, playlistMixer, playlistImg;
                        ArrayList<SpotlightModel> list = new ArrayList<SpotlightModel>();
                        for (int x = 0; x < playlistSize; x++) {
                            spotifyId = featuredPlaylists.playlists.items.get(x).id;
                            playlistTitle = featuredPlaylists.playlists.items.get(x).name;
                            playlistMixer = featuredPlaylists.playlists.items.get(x).owner.id;
                            playlistImg = featuredPlaylists.playlists.items.get(x).images.get(0).url;
                            list.add(new SpotlightModel(spotifyId, playlistTitle, playlistMixer, playlistImg));
                        }
                        mAdapter = new SpotlightAdapter(list);

                        MAIN_THREAD.post(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerSpotlight.setAdapter(mAdapter);
                            }
                        });
                    } else {
                        MAIN_THREAD.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Couldn't get Playlists", Toast.LENGTH_LONG).show();
                                mRecyclerSpotlight.setAdapter(null);
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Can't access the web", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }
}
