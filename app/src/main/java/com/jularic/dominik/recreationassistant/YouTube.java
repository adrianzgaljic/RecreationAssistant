package com.jularic.dominik.recreationassistant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class YouTube extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        WebView webView = (WebView) findViewById(R.id.wv_youtube);
        webView.loadUrl("https://www.youtube.com");
    }
}
