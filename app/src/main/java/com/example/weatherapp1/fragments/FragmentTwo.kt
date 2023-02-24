package com.example.weatherapp1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.weatherapp1.LocationActivity
import com.example.weatherapp1.MainActivity
import com.example.weatherapp1.R

class FragmentTwo : Fragment(R.layout.fragment_two) {
    lateinit var tvWindStrength: TextView
    lateinit var tvWindDirection: TextView
    lateinit var tvVisibility: TextView
    lateinit var tvHumidity: TextView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_two, container, false)
        tvWindStrength = view.findViewById(R.id.tv_wind_strength)
        tvWindDirection = view.findViewById(R.id.tv_wind_direction)
        tvVisibility = view.findViewById(R.id.tv_visibility)
        tvHumidity = view.findViewById(R.id.tv_humidity)
        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as LocationActivity).useCreateFragmentTwo()
    }
}