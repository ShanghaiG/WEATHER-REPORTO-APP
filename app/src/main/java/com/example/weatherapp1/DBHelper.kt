package com.example.weatherapp1


import android.content.ContentValues

import android.content.Context

import android.database.Cursor

import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :

    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {

        val currentLocationQuery = ("CREATE TABLE " + CURRENT_LOCATION + " ("
                + CL_ID + " INTEGER PRIMARY KEY, "
                + CL_WIND_SPEED  + " TEXT,"
                + CL_WIN_DIR  + " TEXT,"
                + CL_SYS_COUNTRY_CODE  + " TEXT,"
                + CL_CITY_NAME  + " TEXT,"
                + CL_VISIBILITY  + " TEXT,"
                + CL_PRESSURE  + " TEXT,"
                + CL_TEMP  + " TEXT,"
                + CL_DESCRIPTION  + " TEXT,"
                + CL_COORD_LAT  + " TEXT,"
                + CL_COORD_LON  + " TEXT,"
                + CL_HUMIDITY  + " TEXT,"
                + CL_TIME  + " TEXT,"
                + CL_FULL_TIME  + " INTEGER"
                + ")")

        val unitQuery = ("CREATE TABLE " + CURRENT_UNIT + " ("
                + CU_ID + " INTEGER PRIMARY KEY, "
                + CU_UNIT  + " TEXT"
                + ")")

        val favouritesQuery = ("CREATE TABLE " + FAVOURITE_LOCATIONS + " ("
                + FL_ID + " INTEGER PRIMARY KEY, "
                + FL_CITY_NAME  + " TEXT,"
                + FL_TEMP  + " TEXT,"
                + FL_DESCRIPTION  + " TEXT,"
                + FL_DT_TXT  + " TEXT"
                + ")")

        // we are calling sqlite
        // method for executing our query

        db.execSQL(currentLocationQuery)
        db.execSQL(unitQuery)
        db.execSQL(favouritesQuery)

    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists

        db.execSQL("DROP TABLE IF EXISTS $CURRENT_LOCATION")
        db.execSQL("DROP TABLE IF EXISTS $CURRENT_UNIT")
        db.execSQL("DROP TABLE IF EXISTS $FAVOURITE_LOCATIONS")

        onCreate(db)

    }

    fun saveCurrentLocation(
    clWindSpeed: String,
    clWinDir: String,
    clSysCountryCode: String,
    clCityName: String,
    clVisibility: String,
    clPressure: String,
    clTemp: String,
    clDescription: String,
    clCoordLat: String,
    clCoordLon: String,
    clHumidity: String,
    clTime: String,
    clFullTime: Int
    ){
        // below we are creating
        // a content values variable

        val values = ContentValues()

        values.put(CL_WIND_SPEED, clWindSpeed)
        values.put(CL_WIN_DIR, clWinDir)
        values.put(CL_SYS_COUNTRY_CODE, clSysCountryCode)
        values.put(CL_CITY_NAME, clCityName)
        values.put(CL_VISIBILITY, clVisibility)
        values.put(CL_PRESSURE, clPressure)
        values.put(CL_TEMP, clTemp)
        values.put(CL_DESCRIPTION, clDescription)
        values.put(CL_COORD_LAT, clCoordLat)
        values.put(CL_COORD_LON, clCoordLon)
        values.put(CL_HUMIDITY, clHumidity)
        values.put(CL_TIME, clTime)
        values.put(CL_FULL_TIME, clFullTime)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database

        val db = this.writableDatabase

        val dbReader = this.readableDatabase

        var checker = dbReader.rawQuery("SELECT * FROM $CURRENT_LOCATION", null)

        if(checker != null && checker.count > 0) {
            db.update(CURRENT_LOCATION, values, "id = 1",  null)
        } else {
            db.insert(CURRENT_LOCATION, null, values)
        }

        db.close()
    }

    fun saveCurrentUnit(
        cuUnit: String,
    ){
        val values = ContentValues()

        values.put(CU_UNIT, cuUnit)

        val db = this.writableDatabase

        val dbReader = this.readableDatabase

        var checker = dbReader.rawQuery("SELECT * FROM $CURRENT_UNIT", null)

        if(checker != null && checker.count > 0) {
            db.update(CURRENT_UNIT, values, "id = 1",  null)
        } else {
            db.insert(CURRENT_UNIT, null, values)
        }

        db.close()
    }

    fun saveFavouriteLocation(
        flCityName: String,
        flTemp: String,
        flDescription: String,
        flDtTxt: String
    ){
        val values = ContentValues()

        values.put(FL_CITY_NAME, flCityName)
        values.put(FL_TEMP, flTemp)
        values.put(FL_DESCRIPTION, flDescription)
        values.put(FL_DT_TXT, flDtTxt)

        val db = this.writableDatabase

        val dbReader = this.readableDatabase

        var checker = dbReader.rawQuery("SELECT * FROM $FAVOURITE_LOCATIONS", null)

        if(checker != null && checker.count > 0) {

            if(checkFavouriteLocation(flCityName)) {
                db.update(FAVOURITE_LOCATIONS, values, "name = ?",  arrayOf(flCityName))
            } else {
                db.insert(FAVOURITE_LOCATIONS, null, values)
            }

        } else {
            db.insert(FAVOURITE_LOCATIONS, null, values)
        }

        db.close()
    }


    fun getCurrentLocation(): Cursor? {

        return try{
            val db = this.readableDatabase
            db.rawQuery("SELECT * FROM $CURRENT_LOCATION", null)
        } catch (e: Throwable) {
            null;
        }

    }

    fun getCurrentUnit(): Cursor? {
        val db = this.readableDatabase

        return db.rawQuery("SELECT * FROM $CURRENT_UNIT", null)
    }

    fun getFavouriteLocations(): Cursor? {
        val db = this.readableDatabase

        return db.rawQuery("SELECT * FROM $FAVOURITE_LOCATIONS", null)
    }

    fun checkFavouriteLocation(cityName: String): Boolean {
        val db = this.readableDatabase

        var checker =  db.rawQuery("SELECT * FROM $FAVOURITE_LOCATIONS WHERE name = ?", arrayOf(cityName))

        if(checker != null && checker.count > 0) {
            return true
        }

        return false
    }

    companion object{
        // here we have defined variables for our database
        // below is variable for database name
        private val DATABASE_NAME = "WEATHER_APP"

        // below is the variable for database version
        private val DATABASE_VERSION = 9

        // below is the variable for table name

        val CURRENT_LOCATION = "current_location"
        val CL_ID = "id"
        val CL_WIND_SPEED = "wind_speed"
        val CL_WIN_DIR = "wind_direction"
        val CL_SYS_COUNTRY_CODE = "country"
        val CL_CITY_NAME = "name"
        val CL_VISIBILITY = "visibility"
        val CL_PRESSURE = "pressure"
        val CL_TEMP = "temperature"
        val CL_DESCRIPTION = "description"
        val CL_COORD_LAT = "lat"
        val CL_COORD_LON = "lon"
        val CL_HUMIDITY = "humidity"
        val CL_TIME = "dt"
        val CL_FULL_TIME = "dt_full"

        val CURRENT_UNIT = "current_unit"
        val CU_ID = "id"
        val CU_UNIT = "unit"

        val FAVOURITE_LOCATIONS = "favourite_locations"
        val FL_ID = "id"
        val FL_CITY_NAME = "name"
        val FL_TEMP = "temperature"
        val FL_DESCRIPTION = "description"
        val FL_DT_TXT = "dt_txt"
    }
}
