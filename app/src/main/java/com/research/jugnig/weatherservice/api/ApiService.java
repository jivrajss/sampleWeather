package com.research.jugnig.weatherservice.api;


import com.research.jugnig.weatherservice.data.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    String BASE_URL = "http://api.openweathermap.org/";
    String location = "London";
    String APP_ID = "97379a6635c2d1da26a58fc6eede8637";

    @GET("/data/2.5/weather/")
    Call<WeatherResponse> getCurrentWeather(@Query("q") String location, @Query("APPID") String appId);

}
