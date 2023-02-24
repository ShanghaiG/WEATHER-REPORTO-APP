package com.example.weatherapp1

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp1.data.ForecastData
import com.example.weatherapp1.data.WeatherData
import com.example.weatherapp1.fragments.FragmentOne
import com.example.weatherapp1.fragments.FragmentThree
import com.example.weatherapp1.fragments.FragmentTwo
import com.example.weatherapp1.models.weather.WeatherModel
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.Date

class LocationActivity: AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val preferenceName: String = "WeatherAppPreference"


    private lateinit var mSharedPreferences: SharedPreferences
    private var selectedCity: String? = null
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

    /**Init fragments  */
    private lateinit var fragmentOne: FragmentOne
    private lateinit var fragmentTwo: FragmentTwo
    private lateinit var fragmentThree: FragmentThree

    private lateinit var weatherData: WeatherData
    private lateinit var forecastData: ForecastData

    private lateinit var context: Context
    private lateinit var resources: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(supportActionBar != null){
            supportActionBar?.hide();
        }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = getResources().getColor(R.color.primary_text_color)

        context = this

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        var isLocationEnabled = isLocationEnabled()

        displayPermissionMessage(mFusedLocationClient, mLocationCallback, isLocationEnabled, this)

        fragmentOne = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as FragmentOne
        fragmentTwo = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as FragmentTwo
        fragmentThree = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3) as FragmentThree

        weatherData = WeatherData(context)
        forecastData = ForecastData(context)

        mSharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE)

        searchButtonListener()
        refreshButtonListener()
        saveButtonListener()

        resources = application.resources.configuration.toString()
    }

    fun displayPermissionMessage(
        mFusedLocationClient: FusedLocationProviderClient,
        mLocationCallback: LocationCallback,
        isLocationEnabled: Boolean,
        context: Context) {

        if(!isLocationEnabled) {
            Toast.makeText(
                context,
                "Your location provider is turned off.Please turn it on",
                Toast.LENGTH_SHORT
            ).show()

            //goes directly to settings(opens it)
            try {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        } else {
            Dexter.withActivity(context as Activity?).withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()) {

                            requestLocationData(
                                mFusedLocationClient,
                                mLocationCallback
                            )
                        }

                        if(report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                context,
                                "You have denied location permission. Please allow it",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions(context)
                    }
                }).onSameThread()
                .check()

        }
    }

    //double underscore if dont use parameters
    fun showRationalDialogForPermissions(context: Context) {
        AlertDialog.Builder(context)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton("Go to settings")
            {_, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {
                    dialog, _ -> dialog.dismiss()
            }.show()
    }

    @SuppressLint("MissingPermission")
    fun requestLocationData(
        mFusedLocationClient: FusedLocationProviderClient,
        mLocationCallback: LocationCallback
    ) {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun isLocationEnabled(): Boolean {
        //access to the system location
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun searchButtonListener() {
        val searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            val inputField = findViewById<TextInputEditText>(R.id.cityInput)
            val inputFieldValue = inputField.text.toString()
            if (inputFieldValue != "") {
                selectedCity = inputFieldValue

                weatherData.getWeather(null, null, inputFieldValue, true, mSharedPreferences, fragmentOne, fragmentTwo, resources)

                forecastData.getForecast(null, null, inputFieldValue, mSharedPreferences, fragmentThree, resources)
            }
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(inputField.windowToken, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshButtonListener() {
        val refreshButton = findViewById<Button>(R.id.action_refresh)

        refreshButton.setOnClickListener {
            if (selectedCity != null && selectedCity.toString() != "") {

                weatherData.getWeather(null, null, selectedCity, true, mSharedPreferences, fragmentOne, fragmentTwo, resources)
                forecastData.getForecast(null, null, selectedCity, mSharedPreferences, fragmentThree, resources)
            } else if (userLatitude != null && userLongitude != null) {

                weatherData.getWeather(userLatitude, userLongitude, null, true, mSharedPreferences, fragmentOne, fragmentTwo, resources)
                forecastData.getForecast(userLatitude, userLongitude, null, mSharedPreferences, fragmentThree, resources)
            } else {

                requestLocationData(mFusedLocationClient, mLocationCallback)
            }
        }  
    }

    private fun saveButtonListener() {
        val saveButton = findViewById<Button>(R.id.save_button_location)

        val db = DBHelper(this, null)

        saveButton.setOnClickListener {
            var weatherResponseJsonString =
                mSharedPreferences.getString("weather_response_data", "")

            val weatherList = Gson().fromJson(weatherResponseJsonString, WeatherModel::class.java)
            Log.i("weatherList", "$weatherList")

            if(weatherList != null) {

                var clWindSpeed = weatherList.wind.speed.toString()
                var clWinDir = weatherList.wind.deg.toString()
                var clSysCountryCode = weatherList.sys.country
                var clCityName = weatherList.name.toString()
                var clVisibility = weatherList.visibility.toString()
                var clPressure = weatherList.main.pressure.toString()
                var clTemp = weatherList.main.temp.toString()
                var clDescription = weatherList.weather[0].description.toString()
                var clCoordLat = weatherList.coord.lat.toString()
                var clCoordLon = weatherList.coord.lon.toString()
                var clHumidity = weatherList.main.humidity.toString()
                var dt = weatherList.dt

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val parseDate = Date(dt.toLong() * 1000)

                var flDtTxt = dateFormat.format(parseDate)

                db.saveCurrentLocation(
                    clWindSpeed,
                    clWinDir,
                    clSysCountryCode,
                    clCityName,
                    clVisibility,
                    clPressure,
                    clTemp,
                    clDescription,
                    clCoordLat,
                    clCoordLon,
                    clHumidity,
                    flDtTxt,
                    dt
                )

                var flCityName = weatherList.name.toString()
                var flTemp = weatherList.main.temp.toString()
                var flDescription = weatherList.weather[0].main.toString()


                if (db.checkFavouriteLocation(flCityName)) {

                    Toast.makeText(
                        context,
                        "City already exists in database",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    db.saveFavouriteLocation(flCityName, flTemp, flDescription, flDtTxt)
                }
            }

        }
    }

    fun useCreateFragmentOne() {
        Log.d("test", "first fragmentCreated")
    }

    fun useCreateFragmentTwo() {
        Log.d("test", "second fragment Created")
    }

    fun useCreateFragmentThree() {
        Log.d("test", "third fragment Created")

        weatherData.setWeatherView(mSharedPreferences, fragmentOne, fragmentTwo, resources)
        forecastData.setForecastView(mSharedPreferences, fragmentThree, resources)
    }



    private val mLocationCallback = object: LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult?) {
            val mLastLocation: Location? = locationResult?.lastLocation
            val latitude = mLastLocation?.latitude

            Log.i("Current latitude", "${latitude}")

            val longitude = mLastLocation?.longitude
            Log.i("Current longitude", "${longitude}")

            userLatitude = latitude
            userLongitude = longitude

            if (longitude != null && latitude != null) {
                weatherData.getWeather(latitude, longitude, null, true, mSharedPreferences, fragmentOne, fragmentTwo, resources)
                forecastData.getForecast(latitude, longitude, null, mSharedPreferences, fragmentThree, resources)
            }
        }
    }

    fun moveToMenuActivity(view: View) {
        val intent = Intent(baseContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

}