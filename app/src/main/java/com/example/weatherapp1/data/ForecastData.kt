package com.example.weatherapp1.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp1.Constants
import com.example.weatherapp1.R
import com.example.weatherapp1.fragments.FragmentThree
import com.example.weatherapp1.helpers.HelperMethods
import com.example.weatherapp1.helpers.ProgressDialog
import com.example.weatherapp1.models.forecast.ForecastModel
import com.example.weatherapp1.services.ForecastService


import com.google.gson.Gson
import retrofit.*

class ForecastData(private val context: Context): AppCompatActivity() {

    val progressDialog = ProgressDialog(context)
    private val helperMethods = HelperMethods()

    fun getForecast(latitude: Double?, longitude: Double?, city: String?, mSharedPreferences: SharedPreferences, fragmentThree: FragmentThree, resources: String) {

        if (Constants.isNetworkAvailable(context) && city == null) {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: ForecastService =
                retrofit.create<ForecastService>(ForecastService::class.java)

            val listCall: Call<ForecastModel> = service.getForecastedWeather(
                latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID, null
            )

            listCall.enqueue(object : Callback<ForecastModel> {
                @SuppressLint("SetTextI18n", "CommitPrefEdits")
                override fun onResponse(
                    response: Response<ForecastModel>,
                    retrofit: Retrofit?
                ) {
                    if (response!!.isSuccess) {

                        progressDialog.hideCustomProgressDialog()
                        val weatherList: ForecastModel? = response.body()

                        val weatherResponseJsonString = Gson().toJson(weatherList)

                        val editor = mSharedPreferences.edit()
                        editor.putString("forecast_request_data", weatherResponseJsonString)
                        editor.apply()
                        setForecastView(mSharedPreferences, fragmentThree, resources)

                        Log.i("Response Result", "$weatherList")
                    } else {
                        Toast.makeText(
                            context,
                            "There was an error with your request",
                            Toast.LENGTH_SHORT
                        ).show()
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure( t: Throwable) {
                    progressDialog.hideCustomProgressDialog()
                }
            })
        } else if (Constants.isNetworkAvailable(context) && city != "") {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: ForecastService =
                retrofit.create(ForecastService::class.java)

            val listCall: Call<ForecastModel> = service.getForecastedWeather(
                null, null, Constants.METRIC_UNIT, Constants.APP_ID, city
            )

            listCall.enqueue(object : Callback<ForecastModel> {
                @SuppressLint("SetTextI18n", "CommitPrefEdits")
                override fun onResponse(
                    response: Response<ForecastModel>,
                    retrofit: Retrofit?
                ) {
                    if (response!!.isSuccess) {
                        progressDialog.hideCustomProgressDialog()

                        val forecastList: ForecastModel? = response.body()

                        val forecastResponseJsonString = Gson().toJson(forecastList)
                        val editor = mSharedPreferences.edit()

                        editor.putString("forecast_request_data", forecastResponseJsonString)
                        editor.apply()
                        setForecastView(mSharedPreferences, fragmentThree, resources)

                        Log.i("Response Result", "$forecastList")
                    } else {
                        Toast.makeText(
                            context,
                            "There was an error with your request",
                            Toast.LENGTH_SHORT
                        ).show()
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(t: Throwable) {
                    progressDialog.hideCustomProgressDialog()
                }
            })
        } else {
            Toast.makeText(
                context,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    @SuppressLint("SetTextI18n")
    fun setForecastView(mSharedPreferences: SharedPreferences, fragmentThree: FragmentThree, resources: String) {
        val forecastResponseJsonString = mSharedPreferences.getString("forecast_request_data", "")

        if (!forecastResponseJsonString.isNullOrEmpty()) {
            val weatherList = Gson().fromJson(forecastResponseJsonString, ForecastModel::class.java)
            var weatherListIndex = 0

            for (i in 5 downTo 0 step 1) {

                if(i == 5) {
                    weatherListIndex = 0
                } else {
                    weatherListIndex += 8
                }

                if(weatherListIndex == weatherList.list.size) weatherListIndex = weatherList.list.size-1

                fragmentThree.tvMain[i]?.text = weatherList.list[weatherListIndex].weather[0].main

                var calculatedTemperature =   helperMethods.calculateTemperatureByUnit(weatherList.list[weatherListIndex].main.temp, context)

                fragmentThree.tvMainDesc[i]?.text = (calculatedTemperature.toInt()).toString() + helperMethods.getUnit(context)
                when (weatherList.list[weatherListIndex].weather[0].main) {
                    "Clear" -> fragmentThree.ivMain[i]?.setImageResource(R.drawable.sunny)
                    "Thunderstorm" -> fragmentThree.ivMain[i]?.setImageResource(R.drawable.storm)
                    "Rain", "Drizzle" -> fragmentThree.ivMain[i]?.setImageResource(R.drawable.rain)
                    "Clouds" -> fragmentThree.ivMain[i]?.setImageResource(R.drawable.cloud)
                    "Snow" -> fragmentThree.ivMain[i]?.setImageResource(R.drawable.snowflake)
                    else -> fragmentThree.ivMain[i]?.setImageResource(R.drawable.cloud)
                }
                val splitDate = weatherList.list[weatherListIndex].dt_txt.split(" ")
                fragmentThree.tvDate[i]?.text = splitDate[0]
                fragmentThree.tvHour[i]?.text = splitDate[1]
            }

        }
    }

}