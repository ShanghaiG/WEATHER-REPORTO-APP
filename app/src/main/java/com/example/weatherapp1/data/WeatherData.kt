package com.example.weatherapp1.data

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp1.Constants
import com.example.weatherapp1.DBHelper
import com.example.weatherapp1.helpers.ProgressDialog
import com.example.weatherapp1.R
import com.example.weatherapp1.fragments.FragmentOne
import com.example.weatherapp1.fragments.FragmentTwo
import com.example.weatherapp1.helpers.HelperMethods
import com.example.weatherapp1.models.weather.*
import com.example.weatherapp1.services.WeatherService
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONArray
import org.json.JSONObject
import retrofit.*
import java.text.SimpleDateFormat
import java.util.*

class WeatherData(private val context: Context): AppCompatActivity() {

    private val db = DBHelper(context, null)

    private val helperMethods = HelperMethods()
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    fun getWeather(latitude: Double?, longitude: Double?, city: String?, shouldOpenView: Boolean, mSharedPreferences: SharedPreferences?, fragmentOne: FragmentOne?, fragmentTwo: FragmentTwo?, resources: String?) {
        val progressDialog = ProgressDialog(context)

        if(Constants.isNetworkAvailable(context)) {

            val cursor = db.getCurrentLocation()

            var listCall: Call<WeatherModel>? = null

            if(cursor != null && cursor.count > 0) {
                cursor!!.moveToFirst()

                var time = cursor.getString(cursor.getColumnIndex(DBHelper.CL_TIME))
                var tempCity = cursor.getString(cursor.getColumnIndex(DBHelper.CL_CITY_NAME))

                if(helperMethods.isDataOlderThan3h(time)) {
                    /** Prepare url */
                    val retrofit : Retrofit = Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        /** Use converter or factory of JSON in order to
                         * bring it into the right format */
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    /** Prepare the service in order to make a call with it*/
                    val service: WeatherService = retrofit
                        .create(WeatherService::class.java)

                    /** Make a list call based onto prepared service above */
                    listCall = service.getWeather(
                        latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID, city
                    )

                    progressDialog.showCustomProgressDialog()
                }

                else if (city != "" && city != null && city != tempCity) {

                    val retrofit : Retrofit = Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service: WeatherService = retrofit
                        .create(WeatherService::class.java)

                    listCall = service.getWeather(
                        latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID, city
                    )

                    progressDialog.showCustomProgressDialog()
                }

                else {
                    progressDialog.showCustomProgressDialog()

                    val clWindSpeed = cursor.getString(cursor.getColumnIndex(DBHelper.CL_WIND_SPEED))
                    val clWinDir = cursor.getString(cursor.getColumnIndex(DBHelper.CL_WIN_DIR))
                    val clSysCountryCode = cursor.getString(cursor.getColumnIndex(DBHelper.CL_SYS_COUNTRY_CODE))
                    val clCityName = cursor.getString(cursor.getColumnIndex(DBHelper.CL_CITY_NAME))
                    val clVisibility = cursor.getString(cursor.getColumnIndex(DBHelper.CL_VISIBILITY))
                    val clPressure = cursor.getString(cursor.getColumnIndex(DBHelper.CL_PRESSURE))
                    val clTemp = cursor.getString(cursor.getColumnIndex(DBHelper.CL_TEMP))
                    val clDescription = cursor.getString(cursor.getColumnIndex(DBHelper.CL_DESCRIPTION))
                    val clCoordLat = cursor.getString(cursor.getColumnIndex(DBHelper.CL_COORD_LAT))
                    val clCoordLon = cursor.getString(cursor.getColumnIndex(DBHelper.CL_COORD_LON))
                    val clHumidity = cursor.getString(cursor.getColumnIndex(DBHelper.CL_HUMIDITY))
                    val clFullTime = cursor.getString(cursor.getColumnIndex(DBHelper.CL_FULL_TIME))

                    val coordObject = JSONObject()
                    coordObject.put("lon", clCoordLon.toDouble())
                    coordObject.put("lat", clCoordLat.toDouble())

                    val weatherObject = JSONObject()
                    weatherObject.put("main", clDescription)

                    val weatherArray = JSONArray()
                    weatherArray.put(weatherObject)

                    val mainObject = JSONObject()
                    mainObject.put("temp", clTemp.toDouble())
                    mainObject.put("pressure", clPressure.toDouble())
                    mainObject.put("humidity", clHumidity.toInt())

                    val windObject = JSONObject()
                    windObject.put("deg", clWinDir.toInt())
                    windObject.put("speed", clWindSpeed.toDouble())

                    val sysObject = JSONObject()
                    sysObject.put("country", clSysCountryCode)

                    val rootObject = JSONObject()
                    rootObject.put("visibility", clVisibility.toInt())
                    rootObject.put("dt", clFullTime)
                    rootObject.put("name", clCityName)
                    rootObject.put("coord", coordObject)
                    rootObject.put("main", mainObject)
                    rootObject.put("sys", sysObject)
                    rootObject.put("wind", windObject)
                    rootObject.put("weather", weatherArray)

                    var jsonParser: JsonParser = JsonParser();
                    var gsonObject: JsonObject =
                        jsonParser.parse(rootObject.toString()) as JsonObject;


                    val jsonWeatherResponse = Gson().toJson(gsonObject)
                    val editor = mSharedPreferences!!.edit()

                    editor.putString("weather_response_data", jsonWeatherResponse)
                    editor.apply()


                    progressDialog.hideCustomProgressDialog()

                    if(shouldOpenView) {
                        setWeatherView(mSharedPreferences!!, fragmentOne!!, fragmentTwo!!, resources!!)
                    }
                }

            } else {

                val retrofit : Retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service: WeatherService = retrofit
                    .create(WeatherService::class.java)

                listCall = service.getWeather(
                    latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID, city
                )

                progressDialog.showCustomProgressDialog()
            }

            if(listCall != null) {
                /** Its part of retrofit interface */
                listCall.enqueue(object: Callback<WeatherModel> {
                    /** Successful HTTP response */
                    @SuppressLint("SetTextI18n", "CommitPrefEdits")
                    override fun onResponse(response: Response<WeatherModel>?, retrofit: Retrofit?) {
                        if(response!!.isSuccess) {

                            progressDialog.hideCustomProgressDialog()

                            val weatherList: WeatherModel = response.body()

                            val jsonWeatherResponse = Gson().toJson(weatherList)

                            val editor = mSharedPreferences!!.edit()

                            editor.putString("weather_response_data", jsonWeatherResponse)
                            editor.apply()

                            if(shouldOpenView) {
                                setWeatherView(mSharedPreferences!!, fragmentOne!!, fragmentTwo!!, resources!!)
                            }

                        } else {
                            when(response.code()) {
                                400 -> {
                                    Log.e("Error 400", "Bad Connection")
                                }
                                404 -> {
                                    progressDialog.hideCustomProgressDialog()
                                    Log.e("Error 404", "Not found")
                                    Toast.makeText(
                                        context,
                                        "Weather for provided city was not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    Log.e("Error", "Error")
                                }
                            }
                        }
                    }
                    /** Failed HTTP response */
                    override fun onFailure(t: Throwable?) {
                        Log.e("Error occurred!", t!!.message.toString())

                        progressDialog.hideCustomProgressDialog()
                    }

                })
            }

        } else {
            Toast.makeText(
                context,
                "You do not have connected to the internet",
                Toast.LENGTH_SHORT
            ).show()

            showNoInternetConnection()

            val cursor = db.getCurrentLocation()

            if(cursor != null && cursor.count > 0) {
                cursor!!.moveToFirst()

                    progressDialog.showCustomProgressDialog()

                    val clWindSpeed = cursor.getString(cursor.getColumnIndex(DBHelper.CL_WIND_SPEED))
                    val clWinDir = cursor.getString(cursor.getColumnIndex(DBHelper.CL_WIN_DIR))
                    val clSysCountryCode = cursor.getString(cursor.getColumnIndex(DBHelper.CL_SYS_COUNTRY_CODE))
                    val clCityName = cursor.getString(cursor.getColumnIndex(DBHelper.CL_CITY_NAME))
                    val clVisibility = cursor.getString(cursor.getColumnIndex(DBHelper.CL_VISIBILITY))
                    val clPressure = cursor.getString(cursor.getColumnIndex(DBHelper.CL_PRESSURE))
                    val clTemp = cursor.getString(cursor.getColumnIndex(DBHelper.CL_TEMP))
                    val clDescription = cursor.getString(cursor.getColumnIndex(DBHelper.CL_DESCRIPTION))
                    val clCoordLat = cursor.getString(cursor.getColumnIndex(DBHelper.CL_COORD_LAT))
                    val clCoordLon = cursor.getString(cursor.getColumnIndex(DBHelper.CL_COORD_LON))
                    val clHumidity = cursor.getString(cursor.getColumnIndex(DBHelper.CL_HUMIDITY))
                    val clFullTime = cursor.getString(cursor.getColumnIndex(DBHelper.CL_FULL_TIME))

                    val coordObject = JSONObject()
                    coordObject.put("lon", clCoordLon.toDouble())
                    coordObject.put("lat", clCoordLat.toDouble())

                    val weatherObject = JSONObject()
                    weatherObject.put("main", clDescription)

                    val weatherArray = JSONArray()
                    weatherArray.put(weatherObject)

                    val mainObject = JSONObject()
                    mainObject.put("temp", clTemp.toDouble())
                    mainObject.put("pressure", clPressure.toDouble())
                    mainObject.put("humidity", clHumidity.toInt())

                    val windObject = JSONObject()
                    windObject.put("deg", clWinDir.toInt())
                    windObject.put("speed", clWindSpeed.toDouble())

                    val sysObject = JSONObject()
                    sysObject.put("country", clSysCountryCode)

                    val rootObject = JSONObject()
                    rootObject.put("visibility", clVisibility.toInt())

                    rootObject.put("dt", clFullTime)
                    rootObject.put("name", clCityName)
                    rootObject.put("coord", coordObject)
                    rootObject.put("main", mainObject)
                    rootObject.put("sys", sysObject)
                    rootObject.put("wind", windObject)
                    rootObject.put("weather", weatherArray)

                var jsonParser: JsonParser = JsonParser();
                var gsonObject: JsonObject =
                    jsonParser.parse(rootObject.toString()) as JsonObject;

                    val jsonWeatherResponse = Gson().toJson(gsonObject)

                    val editor = mSharedPreferences!!.edit()

                    editor.putString("weather_response_data", jsonWeatherResponse)
                    editor.apply()

                    progressDialog.hideCustomProgressDialog()

                    if(shouldOpenView) {
                        setWeatherView(mSharedPreferences!!, fragmentOne!!, fragmentTwo!!, resources!!)
                    }
                } else {

                showNoInternetConnection()

                Toast.makeText(
                    context,
                    "No records inside database",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showNoInternetConnection() {
        AlertDialog.Builder(context)
            .setMessage("Warning! Data may not be valid!")
            .setPositiveButton("I understand")
            {
                    dialog, _ -> dialog.dismiss()
            }
            .setNegativeButton("Cancel") {
                    dialog, _ -> dialog.dismiss()
            }.show()
    }

    @SuppressLint("SetTextI18n")
    fun setWeatherView(mSharedPreferences: SharedPreferences, fragmentOne: FragmentOne, fragmentTwo: FragmentTwo, resources: String) {
        val helperMethods = HelperMethods()

        val weatherResponseJsonString = mSharedPreferences.getString("weather_response_data", "")

        if (!weatherResponseJsonString.isNullOrEmpty()) {
            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherModel::class.java)

            for (i in weatherList.weather.indices) {
                fragmentOne.tvCity.text = weatherList.name
                fragmentOne.tvCountry.text = weatherList.sys.country

                fragmentOne.tvLatitude.text = "lat: " + weatherList.coord.lat.toString()
                fragmentOne.tvLongitude.text = "lon: " + weatherList.coord.lon.toString()

                fragmentOne.tvMain.text = weatherList.weather[i].main
                fragmentOne.tvMainDescription.text = weatherList.weather[i].description

                when (weatherList.weather[i].description) {
                    "Clear" -> fragmentOne.ivMain.setImageResource(R.drawable.sunny)
                    "Thunderstorm" -> fragmentOne.ivMain.setImageResource(R.drawable.storm)
                    "Rain", "Drizzle" -> fragmentOne.ivMain.setImageResource(R.drawable.rain)
                    "Clouds" -> fragmentOne.ivMain.setImageResource(R.drawable.cloud)
                    "Snow" -> fragmentOne.ivMain.setImageResource(R.drawable.snowflake)
                    else -> fragmentOne.ivMain.setImageResource(R.drawable.cloud)
                }
                var calculatedTemperature =  helperMethods.calculateTemperatureByUnit(weatherList.main.temp, context)

                fragmentOne.tvTemp.text =
                    (calculatedTemperature.toInt()).toString() + helperMethods.getUnit(context)

                fragmentOne.tvPressure.text = weatherList.main.pressure.toString() + " hPa"

                fragmentTwo.tvWindStrength.text = weatherList.wind.speed.toString() + " m/s"
                fragmentTwo.tvWindDirection.text = "dir: " + helperMethods.degreesToDirection(weatherList.wind.deg)

                fragmentTwo.tvVisibility.text = (weatherList.visibility / 100).toString() + "%"
                fragmentTwo.tvHumidity.text = "RH: " + weatherList.main.humidity.toString() + "%"
            }
        }
    }
}