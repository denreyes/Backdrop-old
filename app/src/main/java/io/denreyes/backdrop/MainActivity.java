package io.denreyes.backdrop;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.PlayConfig;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.denreyes.backdrop.Playlist.PlaylistAdapter;
import io.denreyes.backdrop.Playlist.PlaylistModel;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener,
        MinimizedControllerFragment.OnPausePlay, MaximizedControllerFragment.OnPausePlay {
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;
    private SpotifyApi mApi;
    private SpotifyService mSpotify;
    private Player mPlayer;
    private boolean mTracksBroadcastIsRegistered;
    private SharedPreferences prefIsPlaying, prefToken;

    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    public static final String BROADCAST_NEXT_TRACK = "io.denreyes.backdrop.nexttrack";
    ArrayList<PlaylistModel> model;
    String username, profileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        prefToken = getSharedPreferences("ACCESS_TOKEN_PREF", MODE_PRIVATE);
        prefIsPlaying = getSharedPreferences("IS_PLAYING_PREF",MODE_PRIVATE);
        mApi = new SpotifyApi();
        mApi = mApi.setAccessToken(prefToken.getString("ACCESS_TOKEN", ""));
        mSpotify = mApi.getService();

        initNav();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SpotlightFragment()).commit();
        mSlidingLayout.setPanelSlideListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.controller_container, new MinimizedControllerFragment()).commit();

        Config playerConfig = new Config(this, prefToken.getString("ACCESS_TOKEN", ""),
                this.getString(R.string.spotify_client_id));
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
    public void onPause() {
        super.onPause();
        if (mTracksBroadcastIsRegistered) {
            this.unregisterReceiver(tracksReceiver);
            mTracksBroadcastIsRegistered = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mTracksBroadcastIsRegistered) {
            this.registerReceiver(tracksReceiver,
                    new IntentFilter(PlaylistAdapter.BROADCAST_PLAYLIST_DATA));
            mTracksBroadcastIsRegistered = true;
        }
    }


    private BroadcastReceiver tracksReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            model = intent.getParcelableArrayListExtra("LIST_TRACKS");
            int newPos = intent.getIntExtra("TRACK_POSITION", -1);
            PlayConfig config = PlayConfig.createFor(intent.getStringArrayListExtra("LIST_SONGS"));
            config.withTrackIndex(newPos);
            mPlayer.play(config);
//            isPlaying = true;
            prefIsPlaying.edit().putBoolean("IS_PLAYING",true).apply();
            mPlayer.addPlayerNotificationCallback(new PlayerNotificationCallback() {
                @Override
                public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
                    if (eventType == EventType.TRACK_CHANGED) {
                        Intent i = new Intent(BROADCAST_NEXT_TRACK);
                        i.putExtra("POSITION", getTrackPosition(playerState.trackUri, model) + 1);
                        sendBroadcast(i);
                    }
                }

                @Override
                public void onPlaybackError(ErrorType errorType, String s) {
                }
            });
        }
    };


    private int getTrackPosition(String trackUri, ArrayList<PlaylistModel> list) {
        for (int x = 0; x < list.size(); x++) {
            if (("spotify:track:" + list.get(x).track_id).equals(trackUri)) {
                return x;
            }
        }
        return 0;
    }

    @Override
    public void onPausePlay(boolean bool) {
        if (bool) {
            mPlayer.pause();
//            isPlaying = false;
            prefIsPlaying.edit().putBoolean("IS_PLAYING", false).apply();
        } else {
            mPlayer.resume();
//            isPlaying = true;
            prefIsPlaying.edit().putBoolean("IS_PLAYING",true).apply();
        }
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

    @Bind(R.id.username)
    TextView mTextNavUser;
    @Bind(R.id.img_profile)
    SimpleDraweeView mImgProfile;

    private void initNav() {
        setupDrawerContent(navigationView);

        mSpotify.getMe(new Callback<User>() {
            @Override
            public void success(User user, Response response) {
                username = user.display_name;
                profileUrl = user.images.get(0).url;
                MAIN_THREAD.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextNavUser.setText(username);
                        mImgProfile.setImageURI(Uri.parse(profileUrl));
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            }
        });
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

    //Controller Methods
    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.controller_container, new MinimizedControllerFragment()).commit();
    }

    @Override
    public void onPanelExpanded(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.controller_container, new MaximizedControllerFragment()).commit();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        prefIsPlaying.edit().putBoolean("IS_PLAYING", false).apply();
    }
}
