package com.jularic.dominik.recreationassistant;

public class Weather {

    private String date;
    private int MinTemperature;
    private int MaxTemperature;
    private String desc;

    public Weather(String date, int minTemperature, int maxTemperature, String desc) {
        this.date = date;
        MinTemperature = minTemperature;
        MaxTemperature = maxTemperature;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return
                date + " \n" +desc +" \n"+
                "Min temperature=" + MinTemperature + " F \n"+
                "Max temperature=" + MaxTemperature + " F ";
    }
}