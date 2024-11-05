package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class started_1 : Fragment() {

    private var skipListener: AdapterSlider.SkipListener? = null

    fun setSkipListener(listener: AdapterSlider.SkipListener?) {
        skipListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_started_1, container, false)

        val skipTextView: TextView = view.findViewById(R.id.skip)
        skipTextView.setOnClickListener {
            skipListener?.onSkip()
        }

        return view
    }
}
