package io.denreyes.backdrop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.denreyes.backdrop.Spotlight.SpotlightAdapter;
import io.denreyes.backdrop.Spotlight.SpotlightModel;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.FeaturedPlaylists;
import kaaes.spotify.webapi.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dj on 9/24/2015.
 */
public class SpotlightFragment extends Fragment {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_spotlight)
    RecyclerView mRecyclerSpotlight;
    @Bind(R.id.backdrop)
    ImageView mImageBackdrop;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    private SpotifyApi mApi;
    private SpotifyService mSpotify;

    private SharedPreferences prefToken;

    private ActionBarDrawerToggle mDrawerToggle;
    private SpotlightAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        initPreferences();
        initSpotify();
        initToolbar();
        initLayoutManager();

        fetchFeaturedPlaylists();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initBackdropImg();
    }

    private void initPreferences() {
        prefToken = getActivity().getSharedPreferences("ACCESS_PREF", getActivity().MODE_PRIVATE);
    }

    private void initSpotify() {
        mApi = new SpotifyApi();
        mApi = mApi.setAccessToken(prefToken.getString("ACCESS_TOKEN", ""));
        mSpotify = mApi.getService();
    }

    private void initToolbar() {
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initLayoutManager() {
        StaggeredGridLayoutManager sglm =
                new StaggeredGridLayoutManager(getResources().getInteger(R.integer.list_column_count),
                        StaggeredGridLayoutManager.VERTICAL);
        mRecyclerSpotlight.setLayoutManager(sglm);
        initNav();
    }


    private void initBackdropImg() {
        SharedPreferences mPrefAmbience = getActivity().getSharedPreferences("AMBIENCE_PREF", getActivity(). MODE_PRIVATE);

        if (mPrefAmbience != null) {
            int prevKey = mPrefAmbience.getInt("AMBIENCE", -1);
            switch (prevKey) {
                case 0:
                    mImageBackdrop.setImageResource(R.drawable.img_rain_white);
                    break;
                case 1:
                    mImageBackdrop.setImageResource(R.drawable.img_cafe_white);
                    break;
                case 2:
                    mImageBackdrop.setImageResource(R.drawable.img_storm_white);
                    break;
                case 3:
                    mImageBackdrop.setImageResource(R.drawable.img_park_white);
                    break;
                case 4:
                    mImageBackdrop.setImageResource(R.drawable.img_night_white);
                    break;
                case 5:
                    mImageBackdrop.setImageResource(R.drawable.img_diner_white);
                    break;
            }
        }
    }

    private void initNav() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        if (navigationView != null) {
            mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, mToolbar,
                    R.string.drawer_open, R.string.drawer_close) {
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };
            drawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
    }

    @OnClick(R.id.fab_drop)
    public void onDropClick() {
        startActivity(new Intent(getActivity(), AmbientActivity.class));
    }

    private void fetchFeaturedPlaylists() {
        mProgressBar.setVisibility(View.VISIBLE);
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
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Couldn't get Playlists", Toast.LENGTH_LONG).show();
                            mRecyclerSpotlight.setAdapter(null);
                            mProgressBar.setVisibility(View.GONE);
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