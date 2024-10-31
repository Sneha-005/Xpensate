// AdapterSlider.kt
package com.example.xpensate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterSlider(fragmentActivity: FragmentActivity, private val fragmentList: List<Fragment>) : FragmentStateAdapter(fragmentActivity) {

    private var skipListener: AdapterSlider.SkipListener? = null
    private var continueListener: AdapterSlider.ContinueListener? = null

    interface SkipListener {
        fun onSkip()
    }

    interface ContinueListener {
        fun onContinue()
    }

    fun setSkipListener(listener: SkipListener) {
        skipListener = listener
    }

    fun setContinueListener(listener: ContinueListener) {
        continueListener = listener
    }

    override fun getItemCount(): Int = fragmentList.size

    override fun createFragment(position: Int): Fragment {
        val fragment = fragmentList[position]

        // Set listeners for skip and continue
        when (fragment) {
            is started_1 -> {
                fragment.setSkipListener(skipListener)
            }
            is started_2 -> {
                fragment.setSkipListener(skipListener)
            }
            is started_3 -> {
                fragment.setSkipListener(skipListener)
            }
            is started_4 -> {
                fragment.setSkipListener(skipListener)
            }
            is started_5 -> {
                fragment.setSkipListener(skipListener)
                fragment.setContinueListener(continueListener)
            }
        }

        return fragment
    }
}