package com.example.weatherapp1.models.weather

data class Weather (
    val id: Int?,
    val main: String, /** Group of weather parameters (Rain, Snow, Extreme etc) */
    val description: String?, /** Weather condition within the group */
    val icon: String? /** Weather icon id */
) : java.io.Serializable