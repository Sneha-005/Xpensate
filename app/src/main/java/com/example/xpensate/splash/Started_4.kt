package com.example.xpensate.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.xpensate.com.example.xpensate.splash.AdapterSlider
import com.example.xpensate.databinding.FragmentStarted4Binding

class started_4 : Fragment() {

    private var _binding: FragmentStarted4Binding? = null
    private val binding get() = _binding!!
    private var skipListener: AdapterSlider.SkipListener? = null

    fun setSkipListener(listener: AdapterSlider.SkipListener) {
        skipListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStarted4Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
