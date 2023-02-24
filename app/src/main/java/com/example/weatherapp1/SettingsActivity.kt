package com.example.weatherapp1

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity: AppCompatActivity() {

    private var unitSelected: String? = "°C"

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if(supportActionBar != null){
            supportActionBar?.hide();
        }

        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = resources.getColor(R.color.primary_text_color)


        val units = mutableListOf<String>("","°C","°F", "K")

        val db = DBHelper(this, null)

        val cursor = db.getCurrentUnit()

        if(cursor != null && cursor.count > 0) {
            cursor!!.moveToFirst()
            unitSelected = cursor.getString(cursor.getColumnIndex(DBHelper.CU_UNIT))

            cursor.close()
        }


        val spinner = findViewById<Spinner>(R.id.spinner)
        if (spinner != null) {


            if(units.contains(unitSelected)) {
                units.remove(unitSelected)
            }

            if(units[0] == "") {
                units[0] = unitSelected.toString()
            }

            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, units)
            spinner.adapter = arrayAdapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        Toast.makeText(this@SettingsActivity, getString(R.string.selected_item) + " " + units[position], Toast.LENGTH_SHORT).show()
                        unitSelected = units[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }
    }

    fun moveToMenuActivity(view: View) {
        val intent = Intent(baseContext, MainActivity::class.java)
        finish()
        startActivity(intent)
    }

    fun saveUnit(view: View) {
        val db = DBHelper(this, null)

        db.saveCurrentUnit(unitSelected!!)

        Toast.makeText(this,  " ${unitSelected} saved in database", Toast.LENGTH_LONG).show()
    }

}