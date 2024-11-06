package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentStarted5Binding

class started_5 : Fragment() {

    private var _binding: FragmentStarted5Binding? = null
    private val binding get() = _binding!!
    private var skipListener: AdapterSlider.SkipListener? = null
    private var continueListener: AdapterSlider.ContinueListener? = null

    fun setSkipListener(listener: AdapterSlider.SkipListener) {
        skipListener = listener
    }

    fun setContinueListener(listener: AdapterSlider.ContinueListener) {
        continueListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStarted5Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
