package com.jularic.dominik.recreationassistant;

import android.os.AsyncTask;
import android.util.Log;

import com.jularic.dominik.recreationassistant.listeners.ApiCallListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCall extends AsyncTask<String, Object, JSONObject>

    {

        private String url;
        private ApiCallListener apiCallListener;

        public ApiCall(String url, ApiCallListener apiCallListener){
        this.url = url;
        this.apiCallListener = apiCallListener;
    }


        @Override
        protected JSONObject doInBackground(String... params) {
        Log.d("TAG", "background");

        JSONObject jsonObject = null;
        try{

            HttpURLConnection yahooHttpConn = (HttpURLConnection) (new URL(url)).openConnection();
            yahooHttpConn.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(yahooHttpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            jsonObject = new JSONObject(response.toString());

        } catch(Exception e){
            Log.d("tag", e.toString());
        }

        return jsonObject;
    }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
        apiCallListener.jsonObjectReady(jsonObject);
    }


    }