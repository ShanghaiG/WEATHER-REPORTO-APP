package com.example.weatherapp1.models.weather

data class WeatherWind (
    val speed: Double, /** Wind speed, units: meter/sec or miles/hour */
    val deg: Int, /** Wind direction in degrees */
    val gust: Double?, /** Wind gust, units: meter/sec or miles/hour */
): java.io.Serializable