package com.example.weatherapp1.services

import com.example.weatherapp1.models.weather.WeatherModel
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

interface WeatherService {

    @GET("2.5/weather")
    fun getWeather(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("units") units: String?,
        @Query("appid") appid: String?,
        @Query("q") q: String?,
    ): Call<WeatherModel>
}

