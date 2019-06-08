package com.demo.lifeconvenientdesign.JsonOperation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

public class Result {
    private List<WeatherInfo> results;

    public List<WeatherInfo> getResults() {
        return results;
    }

    public void setWeathers(List<WeatherInfo> results) {
        this.results = results;
    }
}
