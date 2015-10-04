package io.denreyes.backdrop.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.MainActivity;
import io.denreyes.backdrop.R;
import io.denreyes.backdrop.adapters.PlaylistAdapter;
import io.denreyes.backdrop.model.PlaylistModel;
import io.denreyes.backdrop.datum.CoreContract;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Dj on 9/24/2015.
 */
public class PlaylistFragment extends Fragment {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_playlist)
    RecyclerView mRecyclerPlaylist;
    @Bind(R.id.img_album_art)
    SimpleDraweeView mImgAlbumArt;

    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());

    private SpotifyApi mApi;
    private SpotifyService mSpotify;

    private SharedPreferences prefToken, prefPlaylist;
    private PlaylistAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mLayoutManagerType;

    private static String playlist_id, mixer;
    private ArrayList<PlaylistModel> mList;

    private enum LayoutManagerType {
        LINEAR_LAYOUT_MANAGER
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, rootView);

        if(savedInstanceState!=null) {
            mList = savedInstanceState.getParcelableArrayList("PLAYLIST_LIST");
        }

        initPreferences();
        initSpotify();
        initToolbar();
        initLayoutManager();
        initNav();

        setRecyclerViewLayoutManager(mLayoutManagerType);
        fetchPlaylistTracks();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("PLAYLIST_LIST", mList);
    }

    private void initLayoutManager() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
    }

    private void initPreferences() {
        prefPlaylist = getActivity().getSharedPreferences("PLAYLIST_PREF", getActivity().MODE_PRIVATE);
        prefToken = getActivity().getSharedPreferences("ACCESS_PREF", getActivity().MODE_PRIVATE);
    }

    private void initToolbar() {
        Bundle args = getArguments();
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(args.getString("PLAYLIST_TITLE"));
        mImgAlbumArt.setImageURI(Uri.parse(args.getString("PLAYLIST_IMG")));
        playlist_id = args.getString("PLAYLIST_ID");
        mixer = args.getString("PLAYLIST_MIXER");
    }

    private void initSpotify() {
        mApi = new SpotifyApi();
        mApi = mApi.setAccessToken(prefToken.getString("ACCESS_TOKEN", ""));
        mSpotify = mApi.getService();
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

    private void fetchPlaylistTracks() {
        if(mList == null) {
        if (!prefPlaylist.getString("PLAYLIST_ID", "").equals(playlist_id)) {
            mSpotify.getPlaylistTracks(mixer, playlist_id, new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(final Pager<PlaylistTrack> playlistTrackPager, final Response response) {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            int playlistSize = playlistTrackPager.items.size();

                            if (playlistSize != 0) {
                                String trackTitle, trackArtist, trackImg, trackId;
                                mList = new ArrayList<PlaylistModel>();
                                for (int x = 0; x < playlistSize; x++) {
                                    trackTitle = playlistTrackPager.items.get(x).track.name;

                                    trackArtist = playlistTrackPager.items.get(x).track.artists.get(0).name;
                                    for (int a = 1; a < playlistTrackPager.items.get(x).track.artists.size(); a++)
                                        trackArtist = trackArtist + ", " + playlistTrackPager.items.get(x).track.artists.get(a).name;

                                    trackImg = playlistTrackPager.items.get(x).track.album.images.get(1).url;
                                    trackId = playlistTrackPager.items.get(x).track.id;
                                    mList.add(new PlaylistModel(trackTitle, trackArtist, trackImg, trackId));
                                }
                                mAdapter = new PlaylistAdapter(mList, playlist_id);

                                MAIN_THREAD.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mRecyclerPlaylist.setAdapter(mAdapter);
                                    }
                                });
                            } else {
                                MAIN_THREAD.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), "Couldn't get Playlists", Toast.LENGTH_LONG).show();
                                        mRecyclerPlaylist.setAdapter(null);
                                    }
                                });
                            }
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } else
            fetchFromDb();

        } else {
            mAdapter = new PlaylistAdapter(mList, playlist_id);
            mRecyclerPlaylist.setAdapter(mAdapter);
        }
    }

    private void fetchFromDb() {

        Cursor cursor = getActivity().getContentResolver().query(
                CoreContract.TracksEntry.CONTENT_URI,
                null, null, null, null, null
        );

        cursor.moveToFirst();

        String trackTitle, trackArtist, trackImg, trackId;
        ArrayList<PlaylistModel> list = new ArrayList<PlaylistModel>();
        for (int x = 0; x < cursor.getCount(); x++) {
            trackTitle = cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_TITLE));
            trackArtist = cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_ARTIST));
            trackImg = cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_IMG_URL));
            trackId = cursor.getString(cursor.getColumnIndex(CoreContract.TracksEntry.TRACK_SPOTIFY_ID));
            list.add(new PlaylistModel(trackTitle, trackArtist, trackImg, trackId));

            cursor.moveToNext();
        }
        mAdapter = new PlaylistAdapter(list, playlist_id);
        mRecyclerPlaylist.setAdapter(mAdapter);
    }
}