package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.xpensate.databinding.FragmentStarted1Binding
import com.example.xpensate.databinding.FragmentStarted3Binding

class started_3 : Fragment() {

    private var _binding: FragmentStarted3Binding? = null
    private val binding get() = _binding!!
    private var skipListener: AdapterSlider.SkipListener? = null

    fun setSkipListener(listener: AdapterSlider.SkipListener) {
        skipListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStarted3Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
