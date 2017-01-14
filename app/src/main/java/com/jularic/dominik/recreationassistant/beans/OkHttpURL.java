package com.jularic.dominik.recreationassistant.beans;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dominik on 7.1.2017..
 */

public class OkHttpURL {

    public class TestMain {
        OkHttpClient client = new OkHttpClient();

        // code request code here
        String doGetRequest(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }
}
