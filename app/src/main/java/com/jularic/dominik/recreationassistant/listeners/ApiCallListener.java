package com.jularic.dominik.recreationassistant.listeners;

import org.json.JSONObject;

public interface ApiCallListener {

    void jsonObjectReady(JSONObject jsonObject);
}