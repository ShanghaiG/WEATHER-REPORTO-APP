package com.example.weatherapp1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build

import android.os.Bundle
import android.util.Log
import android.view.View

import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp1.data.WeatherData
import com.example.weatherapp1.fragments.FragmentFour
import com.example.weatherapp1.helpers.HelperMethods
import com.example.weatherapp1.models.weather.WeatherModel
import com.example.weatherapp1.services.WeatherService
import com.google.gson.Gson
import retrofit.Call
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


class FavouritesActivity: AppCompatActivity() {

    private lateinit var context: Context
    private lateinit var fragmentFour: FragmentFour

    private lateinit var flCityName: MutableList<String?>
    private lateinit var flTemp: MutableList<String?>
    private lateinit var flDescription: MutableList<String?>
    private lateinit var flDtTxt: MutableList<String?>
    private var index = 0
    private lateinit var weatherData: WeatherData
    private val preferenceName: String = "WeatherAppPreference"


    private lateinit var mSharedPreferences: SharedPreferences

    private val helperMethods = HelperMethods()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        if(supportActionBar != null){
            supportActionBar?.hide();
        }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.primary_text_color)

        context = this

        mSharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

        fragmentFour = supportFragmentManager.findFragmentById(R.id.fragmentContainerView4) as FragmentFour

        val db = DBHelper(this, null)

        val cursor = db.getFavouriteLocations()

        flCityName = mutableListOf()
        flTemp = mutableListOf()
        flDescription = mutableListOf()
        flDtTxt = mutableListOf()


        if(cursor != null && cursor.count > 0) {
            cursor!!.moveToFirst()


            if(helperMethods.isDataOlderThan3h(cursor.getString(cursor.getColumnIndex(DBHelper.FL_DT_TXT)))) {
                saveNewData(cursor.getString(cursor.getColumnIndex(DBHelper.FL_CITY_NAME)), db)

                while(cursor.moveToNext()){
                    saveNewData(cursor.getString(cursor.getColumnIndex(DBHelper.FL_CITY_NAME)), db)
                }

                var cursor2 = db.getFavouriteLocations()

                if(cursor2 != null && cursor2.count > 0) {
                    cursor2!!.moveToFirst()

                    flCityName.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_CITY_NAME)))
                    flTemp.add(index,cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_TEMP)))
                    flDescription.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_DESCRIPTION)))
                    flDtTxt.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_DT_TXT)))

                    while(cursor2.moveToNext()){
                        index++
                        flCityName.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_CITY_NAME)))
                        flTemp.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_TEMP)))
                        flDescription.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_DESCRIPTION)))
                        flDtTxt.add(index, cursor2.getString(cursor2.getColumnIndex(DBHelper.FL_DT_TXT)))
                    }
                }

            } else {
                flCityName.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_CITY_NAME)))
                flTemp.add(index,cursor.getString(cursor.getColumnIndex(DBHelper.FL_TEMP)))
                flDescription.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_DESCRIPTION)))
                flDtTxt.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_DT_TXT)))

                while(cursor.moveToNext()){
                    index++
                    flCityName.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_CITY_NAME)))
                    flTemp.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_TEMP)))
                    flDescription.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_DESCRIPTION)))
                    flDtTxt.add(index, cursor.getString(cursor.getColumnIndex(DBHelper.FL_DT_TXT)))
                }
            }

            cursor.close()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveNewData(cityName: String, db: DBHelper) {
        weatherData.getWeather(null, null, cityName, false, mSharedPreferences, null, null,null)

        var weatherResponseJsonString =
            mSharedPreferences.getString("weather_response_data", "")

        val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherModel::class.java)
        Log.i("weatherList", "$weatherList")

        var flCityName = weatherList.name.toString()
        var flTemp = weatherList.main.temp.toString()
        var flDescription = weatherList.weather[0].main.toString()
        var dt = weatherList.dt

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val parseDate = Date(dt.toLong() * 1000)

        var flDtTxt = dateFormat.format(parseDate)

        db.saveFavouriteLocation(flCityName, flTemp, flDescription, flDtTxt)
    }

    fun useCreateFragmentFour() {
        Log.d("test", "four fragment Created")

        setFavouritesView(flCityName, flTemp, flDescription, flDtTxt, fragmentFour, index)
    }

    fun moveToMenuActivity(view: View) {
        val intent = Intent(baseContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun showNoInternetConnection() {
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
    fun setFavouritesView(        flCityName: MutableList<String?>,
                                  flTemp: MutableList<String?>,
                                  flDescription: MutableList<String?>,
                                  flDtTxt: MutableList<String?>,
                                   fragmentFour: FragmentFour,
    iterator: Int) {

        if(!Constants.isNetworkAvailable(context)) {
            showNoInternetConnection()
        }

        var index = iterator

        if(index > 8) index = 8

        if(index > 0){
            for (i in index downTo 0 step 1) {

                fragmentFour.tvCity[i]?.text = flCityName[i]

                var calculatedTemperature =   helperMethods.calculateTemperatureByUnit(flTemp[i]!!.toDouble(), context)
                fragmentFour.tvMain[i]?.text = flDescription[i]

                fragmentFour.tvMainDesc[i]?.text = (calculatedTemperature.toInt()).toString() + helperMethods.getUnit(context)
                when (flDescription[i]) {
                    "Clear" -> fragmentFour.ivMain[i]?.setImageResource(R.drawable.sunny)
                    "Thunderstorm" -> fragmentFour.ivMain[i]?.setImageResource(R.drawable.storm)
                    "Rain", "Drizzle" -> fragmentFour.ivMain[i]?.setImageResource(R.drawable.rain)
                    "Clouds" -> fragmentFour.ivMain[i]?.setImageResource(R.drawable.cloud)
                    "Snow" -> fragmentFour.ivMain[i]?.setImageResource(R.drawable.snowflake)
                    else -> fragmentFour.ivMain[i]?.setImageResource(R.drawable.cloud)
                }
                val splitDate = flDtTxt[i]!!.split(" ")
                fragmentFour.tvDate[i]?.text = splitDate[0]
                fragmentFour.tvHour[i]?.text = splitDate[1]

            }

        }
    }


}