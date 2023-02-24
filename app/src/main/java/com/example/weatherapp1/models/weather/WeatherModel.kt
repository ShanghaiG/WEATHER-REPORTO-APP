package com.example.weatherapp1.models.weather

data class WeatherModel (
    val coord: WeatherCoords,
    val weather: List<Weather>,
    val base: String?,/** internal parameter */
    val main: WeatherMain,
    val visibility: Int,/** visibility meter, max 10km */
    val wind: WeatherWind,
    val clouds: WeatherClouds?,
    val dt: Int,/** Time of data calculation, UTC */
    val timezone: Int?,
    val sys: WeatherSys,
    val id: Int?,
    val name: String,/** City name */
    val cod: Int? /** internal parameter */
) : java.io.Serializable