package com.example.xpensate.Fragments.pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterSlider(
    fragmentActivity: FragmentActivity,
    private val fragments: List<Fragment>
) : FragmentStateAdapter(fragmentActivity) {

    private var skipListener: SkipListener? = null
    private var continueListener: ContinueListener? = null

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

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
