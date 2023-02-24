package com.example.weatherapp1.services

import com.example.weatherapp1.models.forecast.ForecastModel
import retrofit.Call
import retrofit.http.GET
import retrofit.http.Query

interface ForecastService {

    @GET("2.5/forecast")
    fun getForecastedWeather(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("units") units: String?,
        @Query("appid") appid: String?,
        @Query("q") q: String?,
    ): Call<ForecastModel>
}