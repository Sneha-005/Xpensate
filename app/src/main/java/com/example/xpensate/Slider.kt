// Slider.kt
package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2

class Slider : Fragment(), AdapterSlider.SkipListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_slider, container, false)
        val viewPager: ViewPager2 = view.findViewById(R.id.viewPager)
        val fragmentList = listOf(started_1(), started_2(), started_3(), started_4(), started_5())
        val adapter = AdapterSlider(requireActivity(), fragmentList)

        adapter.setSkipListener(this)

        adapter.setContinueListener(object : AdapterSlider.ContinueListener {
            override fun onContinue() {
                findNavController().navigate(R.id.action_slider_to_login2) // Ensure this action ID is correct
            }
        })


        viewPager.adapter = adapter

        return view
    }

    override fun onSkip() {
        view?.let {
            findNavController().navigate(R.id.action_slider_to_login2) // Ensure the action ID is correct
        }
    }
}
