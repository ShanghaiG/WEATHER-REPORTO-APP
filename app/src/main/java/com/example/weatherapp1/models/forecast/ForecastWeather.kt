package com.example.weatherapp1.models.forecast

data class ForecastWeather (
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
        ): java.io.Serializable