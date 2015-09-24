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
public class SpotlightFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener {
        @Bind(R.id.toolbar)
        Toolbar mToolbar;
        @Bind(R.id.recycler_spotlight)
        RecyclerView mRecyclerSpotlight;
        @Bind(R.id.backdrop)
        ImageView mImageBackdrop;
        @Bind(R.id.drawer_layout)
        DrawerLayout mDrawerLayout;
        @Bind(R.id.nav_view)
        NavigationView navigationView;
        @Bind(R.id.progress_bar)
        ProgressBar mProgressBar;
        @Bind(R.id.sliding_layout)
        SlidingUpPanelLayout mSlidingLayout;
        private ActionBarDrawerToggle mDrawerToggle;

        private SpotlightAdapter mAdapter;
        private SpotifyApi mApi;
        private SpotifyService mSpotify;
        String username;

        private final String LOG_TAG = SpotlightFragment.class.getSimpleName();
        private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, rootView);
            SharedPreferences prefToken = getActivity().getSharedPreferences("ACCESS_TOKEN_PREF", getActivity().MODE_PRIVATE);
            mApi = new SpotifyApi();
            mApi = mApi.setAccessToken(prefToken.getString("ACCESS_TOKEN", ""));
            mSpotify = mApi.getService();

            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            initNav();
            mSlidingLayout.setPanelSlideListener(this);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.controller_container, new MinimizedControllerFragment()).commit();

            mImageBackdrop.setImageResource(R.drawable.img_storm_white);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(getResources().getInteger(R.integer.list_column_count),
                            StaggeredGridLayoutManager.VERTICAL);
            mRecyclerSpotlight.setLayoutManager(sglm);

            fetchFeaturedPlaylists();
            return rootView;
        }

        private void setupDrawerContent(NavigationView navigationView) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            return true;
                        }
                    });
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

        @Bind(R.id.username)
        TextView mTextNavUser;

        private void initNav() {
            if (navigationView != null) {
                setupDrawerContent(navigationView);

                mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
                    public void onDrawerClosed(View view) {
                        super.onDrawerClosed(view);
                    }

                    /** Called when a drawer has settled in a completely open state. */
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                    }
                };
                mDrawerLayout.setDrawerListener(mDrawerToggle);
                mDrawerToggle.syncState();
            }
            mSpotify.getMe(new Callback<User>() {
                @Override
                public void success(User user, Response response) {
                    username = user.display_name;
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextNavUser.setText(username);
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                }
            });
        }

        //Controller Methods
        @Override
        public void onPanelSlide(View view, float v) {
        }

        @Override
        public void onPanelCollapsed(View view) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.controller_container, new MinimizedControllerFragment()).commit();
        }

        @Override
        public void onPanelExpanded(View view) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.controller_container, new MaximizedControllerFragment()).commit();
        }

        @Override
        public void onPanelAnchored(View view) {

        }

        @Override
        public void onPanelHidden(View view) {
        }
    }