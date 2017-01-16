package com.jularic.dominik.recreationassistant;

import com.jularic.dominik.recreationassistant.listeners.ApiCallListener;
import com.jularic.dominik.recreationassistant.listeners.HttpResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherApiCall implements ApiCallListener {

    private String url;
    private HttpResponseListener httpResponseListener;

    public WeatherApiCall(String url, HttpResponseListener httpResponseListener){
        this.url = url;
        this.httpResponseListener = httpResponseListener;
    }



    public void execute(){
        ApiCall apiCall = new ApiCall(url,this);
        apiCall.execute();
    }


    @Override
    public void jsonObjectReady(JSONObject jsonObject) {
        List<Weather> weatherList = new ArrayList<>();
        try{
            JSONObject jsonWeather = jsonObject.getJSONObject("query").
                    getJSONObject("results");

            JSONArray getContactsArray = new JSONArray(jsonWeather.getString("channel"));
            for(int a =0 ; a < getContactsArray.length(); a++)
            {
                JSONObject getJSonObj = (JSONObject)getContactsArray.get(a);
                String desc = getJSonObj.getJSONObject("item").getJSONObject("forecast").getString("text").toString();
                String date = getJSonObj.getJSONObject("item").getJSONObject("forecast").getString("date").toString();

                int minTemp = Integer.parseInt(getJSonObj.getJSONObject("item").getJSONObject("forecast").get("low").toString());
                int highTemp = Integer.parseInt(getJSonObj.getJSONObject("item").getJSONObject("forecast").get("high").toString());

                weatherList.add(new Weather(date, minTemp, highTemp, desc));

            }



            httpResponseListener.onWeatherResult(weatherList);
        } catch (Exception e){
            httpResponseListener.onWeatherResult(null);

        }

    }
}