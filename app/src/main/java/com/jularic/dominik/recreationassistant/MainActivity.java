package com.jularic.dominik.recreationassistant;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //private FragmentManager mFragmentManager;
    Toolbar mToolbar;
    private Button buttonRunkeeperLogin;
    private Button mBtnStatistics;
    private Button mBtnYouTube;
    private Button mBtnWeather;
    private Button mBtnGpx;
    private WebView webView;

    private View.OnClickListener mBtnStatisticsAddListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            showStatisticsActivity();
        }

    };

    private View.OnClickListener mBtnYouTubeAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showYouTubeActivity();
        }
    };

    private View.OnClickListener mBtnWeatherAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showWeatherActivity();
        }
    };

    private View.OnClickListener mBtnGpxAddListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            showGpxActivity();
        }
    };



    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(AccessToken.getCurrentAccessToken() == null){
            goLoginScreen();
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Button mBtnStatistics = (Button) findViewById(R.id.btn_statistics);
        mBtnStatistics.setOnClickListener(mBtnStatisticsAddListener);
        Button mBtnYouTube = (Button) findViewById(R.id.btn_you_tube);
        mBtnYouTube.setOnClickListener(mBtnYouTubeAddListener);
        Button mBtnWeather = (Button) findViewById(R.id.btn_weather);
        mBtnWeather.setOnClickListener(mBtnWeatherAddListener);
        Button btn_my_events = (Button) findViewById(R.id.btn_my_events);
        Button mBtnGpx = (Button) findViewById(R.id.btn_gpx);
        mBtnGpx.setOnClickListener(mBtnGpxAddListener);

        initBackgroundImage();
        btn_my_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyEvents.class);
                startActivity(intent);
            }
        });

    }

    private void showStatisticsActivity() {
        Intent intent = new Intent(this, StatisticsActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showYouTubeActivity() {
        Intent intent = new Intent(this, YouTube.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showWeatherActivity() {
        Intent intent = new Intent(this, WeatherActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showGpxActivity() {
        Intent intent = new Intent(this, GpxActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void logout(View view) {
        LoginManager.getInstance().logOut();
        goLoginScreen();
    }

    @Override
    public void onClick(View v) {
        buttonRunkeeperLogin.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

    }
    private void initBackgroundImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_login_background);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(background);

    }
}
