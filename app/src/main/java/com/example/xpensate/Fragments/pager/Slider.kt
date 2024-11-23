package com.example.xpensate.Fragments.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.xpensate.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Slider : Fragment(), AdapterSlider.SkipListener {
    private lateinit var viewPager: ViewPager2
    private lateinit var continueButton: Button
    private lateinit var dotIndicator: LinearLayout
    private var continueJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_slider, container, false)
        viewPager = view.findViewById(R.id.viewPager)
        dotIndicator = view.findViewById(R.id.dotIndicator)
        continueButton=view.findViewById(R.id.continueButton)
        val fragmentList = listOf(
            started_1().apply { setSkipListener(this@Slider) },
            started_2().apply { setSkipListener(this@Slider) },
            started_3().apply { setSkipListener(this@Slider) },
            started_4().apply { setSkipListener(this@Slider) },
            started_5().apply { setSkipListener(this@Slider) }
        )

        val adapter = AdapterSlider(requireActivity(), fragmentList)

        val skipTextView: TextView = view.findViewById(R.id.skipText)
        skipTextView.setOnClickListener {
            onSkip()
        }

        adapter.setSkipListener(this)

        val continueButton: Button = view.findViewById(R.id.continueButton)
        continueButton.setOnClickListener{
            continueJob?.cancel()
            continueJob = lifecycleScope.launch {
                delay(500)
                onSkip()
            }
        }

        viewPager.adapter = adapter

        setupDotIndicator(fragmentList.size)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDotIndicator(position, fragmentList.size)
                if (position == fragmentList.size - 1) {
                    continueButton.visibility = View.VISIBLE
                } else {
                    continueButton.visibility = View.GONE
                }
            }
        })

        return view
    }

    private fun setupDotIndicator(size: Int) {
        dotIndicator.removeAllViews()
        for (i in 0 until size) {
            val dot = View(requireContext())
            dot.layoutParams = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.dot_size),
                resources.getDimensionPixelSize(R.dimen.dot_size)
            ).apply {
                setMargins(8, 0, 8, 0)
            }

            dot.setBackgroundResource(R.drawable.unselected_dot)
            dotIndicator.addView(dot)
        }
        if (size > 0) {
            dotIndicator.getChildAt(0).setBackgroundResource(R.drawable.selected_dot)
        }
    }

    private fun updateDotIndicator(position: Int, size: Int) {
        for (i in 0 until size) {
            val dot = dotIndicator.getChildAt(i)
            if (i == position) {
                val params = dot.layoutParams as LinearLayout.LayoutParams
                params.width = resources.getDimensionPixelSize(R.dimen.selected_dot_size)
                params.height = resources.getDimensionPixelSize(R.dimen.selected_dot_size)
                dot.layoutParams = params
                dot.setBackgroundResource(R.drawable.selected_dot)
            } else {
                val params = dot.layoutParams as LinearLayout.LayoutParams
                params.width = resources.getDimensionPixelSize(R.dimen.dot_size)
                params.height = resources.getDimensionPixelSize(R.dimen.dot_size)
                dot.layoutParams = params
                dot.setBackgroundResource(R.drawable.unselected_dot)
            }
        }
    }

    override fun onSkip() {
        view?.let {
            findNavController().navigate(R.id.action_slider_to_login2)
        }
    }

}
