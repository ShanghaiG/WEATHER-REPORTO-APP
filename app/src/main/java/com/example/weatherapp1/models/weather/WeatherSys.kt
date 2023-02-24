package com.example.weatherapp1.models.weather

data class WeatherSys (
    val type: Int?, /** Internal parameter */
    val id: Int?,
    val country: String, /** Country code (GB, JP, PL etc.) */
    val sunrise: Int?, /** Sunrise time in UTC */
    val sunset: Int?, /** Sunset time in UTC */
    val message: Double?,
    val pod: String?
): java.io.Serializable