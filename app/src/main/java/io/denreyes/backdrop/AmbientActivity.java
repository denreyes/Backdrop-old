package io.denreyes.backdrop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.denreyes.backdrop.Ambience.AmbientService;

/**
 * Created by DJ on 8/29/2015.
 */
public class AmbientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambient);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AmbientFragment()).commit();
    }

    public static class AmbientFragment extends Fragment {
        @Bind(R.id.toolbar)
        Toolbar mToolbar;
        @Bind(R.id.img_rain)
        ImageView mImageRain;
        @Bind(R.id.img_cafe)
        ImageView mImageCafe;
        @Bind(R.id.img_storm)
        ImageView mImageStorm;
        @Bind(R.id.img_park)
        ImageView mImagePark;
        @Bind(R.id.img_waves)
        ImageView mImageWaves;
        @Bind(R.id.img_diner)
        ImageView mImageDiner;

        private static int KEY_RAIN = 0;
        private static int KEY_CAFE = 1;
        private static int KEY_STORM = 2;
        private static int KEY_PARK = 3;
        private static int KEY_WAVES = 4;
        private static int KEY_DINER = 5;

        private SharedPreferences mPrefAmbience;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_ambient, container, false);
            ButterKnife.bind(this, rootView);
            ((AmbientActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AmbientActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AmbientActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            mPrefAmbience = getActivity().getSharedPreferences("AMBIENCE_PREF", MODE_PRIVATE);

            activatePrev();

            return rootView;
        }

        @OnClick(R.id.img_rain)
        public void goRain() {
            activateSwitch(KEY_RAIN);
        }

        @OnClick(R.id.img_cafe)
        public void goCafe() {
            activateSwitch(KEY_CAFE);
        }

        @OnClick(R.id.img_storm)
        public void goStorm() {
            activateSwitch(KEY_STORM);
        }

        @OnClick(R.id.img_park)
        public void goPark() {
            activateSwitch(KEY_PARK);
        }

        @OnClick(R.id.img_waves)
        public void goWaves() {
            activateSwitch(KEY_WAVES);
        }

        @OnClick(R.id.img_diner)
        public void goDiner() {
            activateSwitch(KEY_DINER);
        }

        private void activateSwitch(int key) {
            if (mPrefAmbience != null) {
                int prevKey = mPrefAmbience.getInt("AMBIENCE", -1);
                switch (prevKey) {
                    case 0:
                        mImageRain.setImageResource(R.drawable.img_ambient_rain);
                        break;
                    case 1:
                        mImageCafe.setImageResource(R.drawable.img_ambient_cafe);
                        break;
                    case 2:
                        mImageStorm.setImageResource(R.drawable.img_ambient_storm);
                        break;
                    case 3:
                        mImagePark.setImageResource(R.drawable.img_ambient_park);
                        break;
                    case 4:
                        mImageWaves.setImageResource(R.drawable.img_ambient_night);
                        break;
                    case 5:
                        mImageDiner.setImageResource(R.drawable.img_ambient_diner);
                        break;
                }
            }

            mPrefAmbience.edit().putInt("AMBIENCE", key).apply();
            int resId = 0;
            switch (key) {
                case 0:
                    mImageRain.setImageResource(R.drawable.img_activated_rain);
                    resId = R.string.rain_link;
                    break;
                case 1:
                    mImageCafe.setImageResource(R.drawable.img_activated_cafe);
                    resId = R.string.coffee_link;
                    break;
                case 2:
                    mImageStorm.setImageResource(R.drawable.img_activated_storm);
                    resId = R.string.thunder_link;
                    break;
                case 3:
                    mImagePark.setImageResource(R.drawable.img_activated_park);
                    resId = R.string.park_link;
                    break;
                case 4:
                    mImageWaves.setImageResource(R.drawable.img_activated_night);
                    resId = R.string.waves_link;
                    break;
                case 5:
                    mImageDiner.setImageResource(R.drawable.img_activated_diner);
                    resId = R.string.diner_link;
                    break;
            }

            if (resId != 0) {
                Intent i = new Intent(getActivity(), AmbientService.class);
                i.putExtra("AUDIO_LINK", getString(resId));
                getActivity().startService(i);
            }
        }

        private void activatePrev() {
            int prevKey = mPrefAmbience.getInt("AMBIENCE", -1);
            switch (prevKey) {
                case 0:
                    mImageRain.setImageResource(R.drawable.img_activated_rain);
                    break;
                case 1:
                    mImageCafe.setImageResource(R.drawable.img_activated_cafe);
                    break;
                case 2:
                    mImageStorm.setImageResource(R.drawable.img_activated_storm);
                    break;
                case 3:
                    mImagePark.setImageResource(R.drawable.img_activated_park);
                    break;
                case 4:
                    mImageWaves.setImageResource(R.drawable.img_activated_night);
                    break;
                case 5:
                    mImageDiner.setImageResource(R.drawable.img_activated_diner);
                    break;
            }
        }
    }


    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}
