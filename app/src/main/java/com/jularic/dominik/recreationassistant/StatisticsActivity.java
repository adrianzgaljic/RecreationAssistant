package com.jularic.dominik.recreationassistant;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jularic.dominik.recreationassistant.beans.OkHttpURL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private WebView webView;
    private TextView mTvRunningStats;
    Toolbar mtoolbar;

    private static final String TAG = "Runnkeeper";
    private final static String CLIENT_ID = "7fc2c7485a3f41a9bdadb0956852c6b1";
    private final static String CLIENT_SECRET = "a5a3c4f608ae4104a59f49627fa6e24c";
    private final static String CALLBACK_URL = "com.jularic.dominik.recreationassistant.runkeeperapi://RunKeeperIsCallingBack";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
        initBackgroundImage();
        //Force to login on every launch.
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        button = (Button) findViewById(R.id.button);
        webView = (WebView) findViewById(R.id.webView);
        //This is important. JavaScript is disabled by default. Enable it.
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onClick(View v) {
        button.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);

        getAuthorizationCode();
    }

    private void getAuthorizationCode() {
        String authorizationUrl = "https://runkeeper.com/apps/authorize?response_type=code&client_id=%s&redirect_uri=%s";
        authorizationUrl = String.format(authorizationUrl, CLIENT_ID, CALLBACK_URL);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {
                    final String authCode = Uri.parse(url).getQueryParameter("code");
                    webView.setVisibility(View.GONE);
                    getAccessToken(authCode);
                    return true;
                }

                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        webView.loadUrl(authorizationUrl);
    }


    private void getAccessToken(String authCode) {
        String accessTokenUrl = "https://runkeeper.com/apps/token?grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=%s";
        final String finalUrl = String.format(accessTokenUrl, authCode, CLIENT_ID, CLIENT_SECRET, CALLBACK_URL);


        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(finalUrl);

                    HttpResponse response = client.execute(post);

                    String jsonString = EntityUtils.toString(response.getEntity());
                    final JSONObject json = new JSONObject(jsonString);

                    String accessToken = json.getString("access_token");
                    getTotalDistance(accessToken);

                } catch (Exception e) {
                    displayToast("Exception occured:(");
                    e.printStackTrace();
                    resetUi();
                }

            }
        });

        networkThread.start();
    }

    private void getTotalDistance(String accessToken) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://api.runkeeper.com/records");

            get.addHeader("Authorization", "Bearer " + accessToken);
            get.addHeader("Accept", "*/*");

            HttpResponse response = client.execute(get);

            String jsonString = EntityUtils.toString(response.getEntity());
            JSONArray jsonArray = new JSONArray(jsonString);
            Log.d(TAG, jsonArray.toString());
            //findTotalWalkingDistance(jsonArray);
            findTotalRunningDistance(jsonArray);

        } catch (Exception e) {
            displayToast("Exception occured:(");
            e.printStackTrace();
            resetUi();
        }
    }

    private void findTotalWalkingDistance(JSONArray arrayOfRecords) {
        try {
            //Each record has activity_type and array of statistics. Traverse to  activity_type = Walking
            for (int ii = 0; ii < arrayOfRecords.length(); ii++) {
                JSONObject statObject = (JSONObject) arrayOfRecords.get(ii);
                if ("Walking".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalWalkingDistanceMeters = iWalkingStat.getLong("value");
                            double totalWalkingDistanceMiles = totalWalkingDistanceMeters * 0.00062137;
                            displayTotalWalkingDistance(totalWalkingDistanceMiles);
                            return;
                        }
                    }
                }
            }
            displayToast("Something went wrong!!!");
        } catch (JSONException e) {
            displayToast("Exception occured:(");
            e.printStackTrace();
            resetUi();
        }
    }

    private void findTotalRunningDistance(JSONArray arrayOfRecords) {
        try {
            //Each record has activity_type and array of statistics. Traverse to  activity_type = Walking
            for (int ii = 0; ii < arrayOfRecords.length(); ii++) {
                JSONObject statObject = (JSONObject) arrayOfRecords.get(ii);
                if ("Running".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalRunningDistanceMeters = iWalkingStat.getLong("value");
                            double totalRunningDistanceKiloMeters = totalRunningDistanceMeters * 0.001;
                            //int totalRunningDistanceKiloMetersInt = (int)totalRunningDistanceKiloMeters;
                            //double totalRunningDistanceMeters = totalRunningDistanceMeters.do
                            //double totalWalkingDistanceMiles = totalWalkingDistanceMeters * 0.00062137;
                            //displayTotalWalkingDistance(totalWalkingDistanceMiles);
                            displayTotalRunningDistance(totalRunningDistanceKiloMeters);
                            displayTvRunning(totalRunningDistanceKiloMeters);
                            return;
                        }
                    }
                }
            }
            displayToast("Something went wrong!!!");
        } catch (JSONException e) {
            displayToast("Exception occured:(");
            e.printStackTrace();
            resetUi();
        }
    }

    private void findTotalStats(JSONArray arrayOfRecords) {
        try {
            //Each record has activity_type and array of statistics. Traverse to  activity_type = Walking
            for (int ii = 0; ii < arrayOfRecords.length(); ii++) {
                JSONObject statObject = (JSONObject) arrayOfRecords.get(ii);
                if ("Running".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalRunningDistanceMeters = iWalkingStat.getLong("value");
                            double totalRunningDistanceKiloMeters = totalRunningDistanceMeters * 0.001;
                            displayTvRunning(totalRunningDistanceKiloMeters);
                            //return;
                        }
                    }
                }
                else if ("Walking".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalWalkingDistanceMeters = iWalkingStat.getLong("value");
                            long totalRunningDistanceMeters = iWalkingStat.getLong("value");
                            double totalRunningDistanceKiloMeters = totalRunningDistanceMeters * 0.001;
                            displayTvWalking(totalRunningDistanceKiloMeters);
                            //return;
                        }
                    }
                }
                else if ("Mountain Biking".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalWalkingDistanceMeters = 0;
                            totalWalkingDistanceMeters = iWalkingStat.getLong("value");
                            long totalRunningDistanceMeters = iWalkingStat.getLong("value");
                            double totalRunningDistanceKiloMeters = totalRunningDistanceMeters * 0.001;
                            displayTvCycling(totalRunningDistanceKiloMeters);
                            //return;
                        }
                    }
                }
                else if ("Swimming".equalsIgnoreCase(statObject.getString("activity_type"))) {
                    //Each activity_type has array of stats, navigate to "Overall" statistic to find the total distance walked.
                    JSONArray walkingStats = statObject.getJSONArray("stats");
                    for (int jj = 0; jj < walkingStats.length(); jj++) {
                        JSONObject iWalkingStat = (JSONObject) walkingStats.get(jj);
                        if ("Overall".equalsIgnoreCase(iWalkingStat.getString("stat_type"))) {
                            long totalWalkingDistanceMeters = iWalkingStat.getLong("value");
                            long totalRunningDistanceMeters = iWalkingStat.getLong("value");
                            double totalRunningDistanceKiloMeters = totalRunningDistanceMeters * 0.001;
                            displayTvSwimming(totalRunningDistanceKiloMeters);
                            //return;
                        }
                    }
                }
                return;
            }
            displayToast("Something went wrong!!!");
        } catch (JSONException e) {
            displayToast("Exception occured:(");
            e.printStackTrace();
            resetUi();
        }
    }

    private void displayTvWalking(final double totalRunningDistanceKiloMeters) {
        runOnUiThread(new Runnable() {
            //String tRdKmString = totalRunningDistanceKiloMeters.toString();
            @Override
            public void run() {
                String stringDistanceKiloMeters= Double.toString(totalRunningDistanceKiloMeters);
                TextView mTvRunningStats = (TextView) findViewById(R.id.tv_walking_stats);
                mTvRunningStats.setText(stringDistanceKiloMeters);
            }
        });
    }
    private void displayTvCycling(final double totalRunningDistanceKiloMeters) {
        runOnUiThread(new Runnable() {
            //String tRdKmString = totalRunningDistanceKiloMeters.toString();
            @Override
            public void run() {
                String stringDistanceKiloMeters= Double.toString(totalRunningDistanceKiloMeters);
                TextView mTvRunningStats = (TextView) findViewById(R.id.tv_cycling_stats);
                mTvRunningStats.setText(stringDistanceKiloMeters);
            }
        });
    }
    private void displayTvSwimming(final double totalRunningDistanceKiloMeters) {
        runOnUiThread(new Runnable() {
            //String tRdKmString = totalRunningDistanceKiloMeters.toString();
            @Override
            public void run() {
                String stringDistanceKiloMeters= Double.toString(totalRunningDistanceKiloMeters);
                TextView mTvRunningStats = (TextView) findViewById(R.id.tv_swimming_stats);
                mTvRunningStats.setText(stringDistanceKiloMeters);
            }
        });
    }

    private double getValues(double totalRunningDistanceKiloMeters) {
        return totalRunningDistanceKiloMeters;
    }

    private void displayTotalRunningDistance(double totalRunningDistanceMeters) {
        final String milesWalkedMessage = (totalRunningDistanceMeters < 1) ? "0 miles?, You get no respect, Start walking already!!!" :
                String.format("Cool, You have runned %.2f km so far.", totalRunningDistanceMeters);
        displayToast(milesWalkedMessage);
        resetUi();
    }


    private void resetUi(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                button.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
        });
    }

    private void displayTotalWalkingDistance(double totalWalkingDistanceMiles) {
        final String milesWalkedMessage = (totalWalkingDistanceMiles < 1) ? "0 miles?, You get no respect, Start walking already!!!" :
                String.format("Cool, You have walked %.2f km  so far.", totalWalkingDistanceMiles);

        displayToast(milesWalkedMessage);
        resetUi();
    }

    private void displayToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayTvRunning(final double totalRunningDistanceKiloMeters) {
        runOnUiThread(new Runnable() {
            //String tRdKmString = totalRunningDistanceKiloMeters.toString();
            @Override
            public void run() {
                String stringDistanceKiloMeters= Double.toString(totalRunningDistanceKiloMeters);
                TextView mTvRunningStats = (TextView) findViewById(R.id.tv_running_stats);
                mTvRunningStats.setText(stringDistanceKiloMeters);
            }
        });
    }

    private void initBackgroundImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_statistics_background);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(background);
    }


}