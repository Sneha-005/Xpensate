package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentResetBinding

class Reset : Fragment() {
    private var _binding: FragmentResetBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using ViewBinding
        _binding = FragmentResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the NavController
        navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to the splash screen
                navController.navigate(R.id.action_reset_to_login2)
            }
        })

        // Set up click listeners
        binding.signup.setOnClickListener {
            navController.navigate(R.id.action_reset_to_sign_up)
        }

        binding.oldAccount.setOnClickListener {
            navController.navigate(R.id.action_reset_to_sign_up)
        }

        binding.loginButton.setOnClickListener {
            navController.navigate(R.id.action_reset_to_login2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to avoid memory leaks
    }
}
