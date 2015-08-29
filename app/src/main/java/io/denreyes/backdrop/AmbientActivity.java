package io.denreyes.backdrop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by DJ on 8/29/2015.
 */
public class AmbientActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AmbientFragment()).commit();
    }

    public static class AmbientFragment extends Fragment {
        @Bind(R.id.toolbar)
        Toolbar mToolbar;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ambient,container,false);
            ButterKnife.bind(this, rootView);
            ((AmbientActivity)getActivity()).setSupportActionBar(mToolbar);
            ((AmbientActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AmbientActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

            return rootView;
        }
    }
}
