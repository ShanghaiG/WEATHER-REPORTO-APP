package com.example.weatherapp1.models.forecast

data class ForecastCity (
    val coord: ForecastCoords,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
        ): java.io.Serializable