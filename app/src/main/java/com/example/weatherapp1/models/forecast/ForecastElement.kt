package com.example.weatherapp1.models.forecast

data class ForecastElement (
    val clouds: ForecastClouds,
    val dt: Int,
    val dt_txt: String,
    val main: ForecastMain,
    val pop: Double,
    val rain: ForecastRain,
    val sys: ForecastSys,
    val visibility: Int,
    val weather: List<ForecastWeather>,
    val wind: ForecastWind
        ): java.io.Serializable