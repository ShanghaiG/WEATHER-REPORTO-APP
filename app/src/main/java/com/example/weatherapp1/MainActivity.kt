package com.example.weatherapp1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import android.view.Window
import android.view.WindowManager


class MainActivity : AppCompatActivity() {

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        if(supportActionBar != null){
            supportActionBar?.hide();
        }


        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.primary_text_color)

        context = this

    }

    fun moveToLocationActivity(view: View){
        val intent = Intent(baseContext, LocationActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun moveToSettingsActivity(view: View) {
        val intent = Intent(baseContext, SettingsActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun moveToFavouritesActivity(view: View) {
        val intent = Intent(baseContext, FavouritesActivity::class.java)
        finish()
        startActivity(intent)
    }
}
