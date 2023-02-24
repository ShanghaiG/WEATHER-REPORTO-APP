package com.example.weatherapp1.models.forecast

data class ForecastModel (
    val city: ForecastCity,
    val cnt: Int,
    val cod: String,
    val list: List<ForecastElement>,
    val message: Int
        ): java.io.Serializable