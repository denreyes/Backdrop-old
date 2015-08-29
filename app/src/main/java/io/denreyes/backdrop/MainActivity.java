package io.denreyes.backdrop;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.denreyes.backdrop.model.SpotlightAdapter;
import io.denreyes.backdrop.model.SpotlightModel;
import io.denreyes.backdrop.view.IMainView;

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

    public static class MainFragment extends Fragment implements IMainView{
        @Bind(R.id.toolbar)
        Toolbar mToolbar;
        @Bind(R.id.recycler_spotlight)
        RecyclerView mRecyclerSpotlight;
        @Bind(R.id.backdrop)
        ImageView mImageBackdrop;

        SpotlightAdapter mAdapter;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main,container,false);
            ButterKnife.bind(this, rootView);
            ((MainActivity)getActivity()).setSupportActionBar(mToolbar);
            ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

            mImageBackdrop.setImageResource(R.drawable.img_rain_white);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(getResources().getInteger(R.integer.list_column_count),
                            StaggeredGridLayoutManager.VERTICAL);
            mRecyclerSpotlight.setLayoutManager(sglm);

            doMock();

            return rootView;
        }

        @OnClick(R.id.fab_drop)
        public void onDropClick(){
            startActivity(new Intent(getActivity(),AmbientActivity.class));
        }

        private void doMock() {
            ArrayList<SpotlightModel> test = new ArrayList<SpotlightModel>();

            test.add(new SpotlightModel("You Make Your Own Luck","GroovyMutant","http://images.8tracks.com/cover/i/008/819/312/Screen_Shot_2015-03-19_at_2.43.15_PM-resized-768.png?rect=1,0,400,400&q=98&fm=jpg&fit=max"));
            test.add(new SpotlightModel("Bay to Breakers","Staff Picks","http://images.8tracks.com/cover/i/001/533/509/tumblr_mnipk1J0LI1qzgkj8o1_500-4084.jpg?rect=0,125,500,500&q=98&fm=jpg&fit=max"));
            test.add(new SpotlightModel("I'll be good","Flaviaffn","http://images.8tracks.com/cover/i/000/425/834/tumblr_mfm73difC01qcfzeko1_500-7180.jpg?rect=0,0,527,527&q=98&fm=jpg&fit=max"));
            test.add(new SpotlightModel("Harmonious Hundred - Indie Dance Party", "HarmonicVibration", "http://images.8tracks.com/imgix/i/008/724/358/giphy-993.gif?rect=0,153,500,500&q=65&fit=max"));
            test.add(new SpotlightModel("no control", "electraheartss","http://images.8tracks.com/cover/i/002/838/077/10693739_1570891199799803_284660400_n-3276.jpg?rect=0,0,640,640&q=98&fm=jpg&fit=max"));
            test.add(new SpotlightModel("club goin' up", "nobodycaress","http://images.8tracks.com/cover/i/002/798/971/tumblr_lyp0usBEcG1qahf1ro1_500-698.gif?rect=74,0,352,352&q=98&fm=jpg&fit=max"));
            test.add(new SpotlightModel("Tonight's Party","Staff Picks","http://images.8tracks.com/cover/i/001/996/643/tumblr_n3cyjdGMeO1sknn2po1_1280-8169.jpg?rect=245,0,491,491&q=98&fm=jpg&fit=max"));
            test.add(new SpotlightModel("Mornin'!","sophie.delcampo", "http://images.8tracks.com/cover/i/000/978/909/coffee-4581.jpg?rect=0,0,500,500&q=98&fm=jpg&fit=max"));


            mAdapter = new SpotlightAdapter(test);
            mRecyclerSpotlight.setAdapter(mAdapter);
        }
    }
}
