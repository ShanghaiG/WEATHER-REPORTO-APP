package com.example.weatherapp1.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapp1.DBHelper
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class HelperMethods {

    @SuppressLint("Range")
    fun getUnit(context: Context): String {
        var unitValue = "°C"
        val db = DBHelper(context, null)

        val cursor = db.getCurrentUnit()

        if(cursor != null && cursor.count > 0) {
            cursor!!.moveToFirst()
            unitValue = cursor.getString(cursor.getColumnIndex(DBHelper.CU_UNIT))

            cursor.close()
        }

        return unitValue
    }

    fun degreesToDirection(degrees: Int): String {
        val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")

        return directions[(degrees / 45 + 0.5).toInt() % 8]
    }

    fun calculateTemperatureByUnit(temperature: Double, context: Context): Double {
        var unit = getUnit(context)

        return when (unit) {
            "°C" -> {
                temperature
            }
            "°F" -> {
                (9/5) * temperature + 32
            }
            "K" -> {
                temperature + 273.15
            }
            else -> { temperature }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isDataOlderThan3h(oldDate: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        val currentDate = Date()

         val parsedOldDate = dateFormat.parse(oldDate)

         val difference: Long = currentDate.time - parsedOldDate.time
         val seconds = difference / 1000
         val minutes = seconds / 60
         val hours = minutes / 60

        if(hours >= 3) {
            return true
        }

        return false
    }

}