package com.jularic.dominik.recreationassistant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

// COMPLETED (1) Add new Activity called SettingsActivity using Android Studio wizard
/**
 * Loads the SettingsFragment and handles the proper behavior of the up button.
 */
public class SettingsActivity extends AppCompatActivity {

    Toolbar mToolbarWeatherSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        mToolbarWeatherSettings = (Toolbar) findViewById(R.id.toolbar_weather);
        setSupportActionBar(mToolbarWeatherSettings);
        // COMPLETED (2) Set setDisplayHomeAsUpEnabled to true on the support ActionBar
        //mToolbarWeatherSettings.set
        //this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}