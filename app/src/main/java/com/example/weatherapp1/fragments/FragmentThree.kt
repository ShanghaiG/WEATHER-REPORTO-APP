package com.example.weatherapp1.fragments

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.example.weatherapp1.LocationActivity
import com.example.weatherapp1.MainActivity
import com.example.weatherapp1.R
import com.google.android.material.textview.MaterialTextView

class FragmentThree : Fragment() {


    lateinit var tvMain: MutableList<TextView?>
    lateinit var tvMainDesc: MutableList<TextView?>
    lateinit var ivMain: MutableList<ImageView?>
    lateinit var tvDate: MutableList<TextView?>
    lateinit var tvHour: MutableList<TextView?>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_three, container, false)

        tvMain = mutableListOf()
        tvMainDesc = mutableListOf()
        ivMain = mutableListOf()
        tvDate = mutableListOf()
        tvHour = mutableListOf()

        for (i in 6 downTo 1 step 1) {
            var tvMainString = "tv_main$i"
            var tvMainDescString = "tv_main_description$i"
            var ivMainString = "iv_main$i"
            var tvDateString = "tv_date$i"
            var tvHourString = "tv_hour$i"

            if(i == 1) {
                tvMainString = "tv_main"
                tvMainDescString = "tv_main_description"
                ivMainString = "iv_main"
                tvDateString = "tv_date"
                tvHourString = "tv_hour"
            }

            var tvMainElement = resources.getIdentifier(tvMainString, "id", context?.packageName)
            var tvMainDescElement = resources.getIdentifier(tvMainDescString, "id", context?.packageName)
            var ivMainElement = resources.getIdentifier(ivMainString, "id", context?.packageName)
            var tvDateElement = resources.getIdentifier(tvDateString, "id", context?.packageName)
            var tvHourElement = resources.getIdentifier(tvHourString, "id", context?.packageName)

            var tvMainView = view.findViewById(tvMainElement) as MaterialTextView?
            var tvMainDescView = view.findViewById(tvMainDescElement) as MaterialTextView?
            var ivMainView = view.findViewById(ivMainElement) as ImageView?
            var tvDateView = view.findViewById(tvDateElement) as MaterialTextView?
            var tvHourView = view.findViewById(tvHourElement) as MaterialTextView?

            tvMain.add(tvMainView)
            tvMainDesc.add(tvMainDescView)
            ivMain.add(ivMainView)
            tvDate.add(tvDateView)
            tvHour.add(tvHourView)

        }

        return view
    }

    override fun onStart() {
        super.onStart()
        (activity as LocationActivity).useCreateFragmentThree()
    }
}