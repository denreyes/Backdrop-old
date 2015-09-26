package io.denreyes.backdrop;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity implements SlidingUpPanelLayout.PanelSlideListener {
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mSlidingLayout;
    private SpotifyApi mApi;
    private SpotifyService mSpotify;

    private static final Handler MAIN_THREAD = new Handler(Looper.getMainLooper());
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferences prefToken = getSharedPreferences("ACCESS_TOKEN_PREF", MODE_PRIVATE);
        mApi = new SpotifyApi();
        mApi = mApi.setAccessToken(prefToken.getString("ACCESS_TOKEN", ""));
        mSpotify = mApi.getService();

        initNav();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new SpotlightFragment()).commit();
        mSlidingLayout.setPanelSlideListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.controller_container, new MinimizedControllerFragment()).commit();
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

    private void initNav() {
        setupDrawerContent(navigationView);

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
}
