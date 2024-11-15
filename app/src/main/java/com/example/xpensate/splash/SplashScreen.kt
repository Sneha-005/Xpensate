package com.example.xpensate.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.delay
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.xpensate.R
import kotlinx.coroutines.launch


class splashScreen : Fragment() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)

        lifecycleScope.launch {
            delay(2000)
            TokenDataStore.getAccessToken(requireContext()).collect { accessToken ->
                if (accessToken != null) {
                    safeNavigate(R.id.action_splashScreen_to_blankFragment)
                } else {
                    safeNavigate(R.id.action_splashScreen_to_slider)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    private fun safeNavigate(actionId: Int) {
        if (findNavController().currentDestination?.id == R.id.splashScreen) {
            findNavController().navigate(actionId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        return view


    }
}



