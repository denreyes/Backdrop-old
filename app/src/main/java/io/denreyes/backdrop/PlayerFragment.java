package io.denreyes.backdrop;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.Playlist.PlaylistAdapter;
import io.denreyes.backdrop.Playlist.PlaylistModel;
import io.denreyes.backdrop.Playlist.TracksContract;
import io.denreyes.backdrop.Playlist.TracksDBHelper;
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
public class PlayerFragment extends Fragment implements SlidingUpPanelLayout.PanelSlideListener{
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_playlist)
    RecyclerView mRecyclerPlaylist;
    @Bind(R.id.img_album_art)
    SimpleDraweeView mImgAlbumArt;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;

    SharedPreferences prefToken, prefPlaylist;
    PlaylistAdapter mAdapter;
    private SpotifyApi mApi;
    private SpotifyService mSpotify;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected LayoutManagerType mLayoutManagerType;
    private boolean mBroadcastIsRegistered;
    private final String LOG_TAG = PlayerFragment.class.getSimpleName();
    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    String playlist_id, mixer;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        prefPlaylist = getActivity().getSharedPreferences("PLAYLIST_PREF", getActivity().MODE_PRIVATE);
        prefToken = getActivity().getSharedPreferences("ACCESS_TOKEN_PREF", getActivity().MODE_PRIVATE);
        mApi = new SpotifyApi();
        mApi = mApi.setAccessToken(prefToken.getString("ACCESS_TOKEN", ""));
        mSpotify = mApi.getService();

        ((PlayerActivity) getActivity()).setSupportActionBar(mToolbar);
        ((PlayerActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((PlayerActivity) getActivity()).getSupportActionBar().setTitle(intent.getStringExtra("PLAYLIST_TITLE"));
        mImgAlbumArt.setImageURI(Uri.parse(intent.getStringExtra("PLAYLIST_IMG")));
        playlist_id = intent.getStringExtra("PLAYLIST_ID");
        mixer = intent.getStringExtra("PLAYLIST_MIXER");

        mSlidingLayout.setPanelSlideListener(this);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.controller_container, new MinimizedControllerFragment()).commit();

        setRecyclerViewLayoutManager(mLayoutManagerType);

        fetchPlaylistTracks();

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

    private void fetchPlaylistTracks() {
        if(!prefPlaylist.getString("PLAYLIST_ID","").equals(playlist_id)) {
            mSpotify.getPlaylistTracks(mixer, playlist_id, new Callback<Pager<PlaylistTrack>>() {
                @Override
                public void success(final Pager<PlaylistTrack> playlistTrackPager, final Response response) {
                    MAIN_THREAD.post(new Runnable() {
                        @Override
                        public void run() {
                            int playlistSize = playlistTrackPager.items.size();

                            if (playlistSize != 0) {
                                String trackTitle, trackArtist, trackImg, trackId;
                                ArrayList<PlaylistModel> list = new ArrayList<PlaylistModel>();
                                for (int x = 0; x < playlistSize; x++) {
                                    trackTitle = playlistTrackPager.items.get(x).track.name;

                                    trackArtist = playlistTrackPager.items.get(x).track.artists.get(0).name;
                                    for (int a = 1; a < playlistTrackPager.items.get(x).track.artists.size(); a++)
                                        trackArtist = trackArtist + ", " + playlistTrackPager.items.get(x).track.artists.get(a).name;

                                    trackImg = playlistTrackPager.items.get(x).track.album.images.get(1).url;
                                    trackId = playlistTrackPager.items.get(x).track.id;
                                    list.add(new PlaylistModel(trackTitle, trackArtist, trackImg, trackId));
                                }
                                mAdapter = new PlaylistAdapter(list, playlist_id);

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
        }else{
            SQLiteDatabase db = new TracksDBHelper(getActivity()).getWritableDatabase();
            Cursor cursor = db.query(
                    TracksContract.TracksEntry.TABLE_NAME,
                    null, null, null, null, null, null
            );
            cursor.moveToFirst();

            String trackTitle, trackArtist, trackImg, trackId;
            ArrayList<PlaylistModel> list = new ArrayList<PlaylistModel>();
            for (int x = 0; x < cursor.getCount(); x++) {
                trackTitle = cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_TITLE));
                trackArtist = cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_ARTIST));
                trackImg = cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_IMG_URL));
                trackId = cursor.getString(cursor.getColumnIndex(TracksContract.TracksEntry.TRACK_SPOTIFY_ID));
                list.add(new PlaylistModel(trackTitle, trackArtist, trackImg, trackId));

                cursor.moveToNext();
            }
            mAdapter = new PlaylistAdapter(list, playlist_id);
            mRecyclerPlaylist.setAdapter(mAdapter);
        }
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