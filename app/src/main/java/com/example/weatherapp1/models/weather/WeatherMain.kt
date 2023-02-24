package com.example.weatherapp1.models.weather


data class WeatherMain (
    val temp: Double, /** Temperature (units available: Kelvin(default), Celsius, Fahrenheit)*/
    val pressure: Double, /** Atmospheric pressure, hPa*/
    val humidity: Int, /** Humidity in % */
    val temp_min: Double?, /** Min temperature at the moment */
    val temp_max: Double?, /** Max temperature at the moment */
    val sea_level: Double?, /** Atmospheric pressure on the sea level, hPa */
    val grnd_level: Double? /** Atmospheric pressure on the ground level, hPa */
): java.io.Serializable