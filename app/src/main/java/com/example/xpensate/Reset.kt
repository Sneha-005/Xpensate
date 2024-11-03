package com.example.xpensate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentResetBinding
import com.example.xpensate.network.AuthInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Reset : Fragment() {
    private var _binding: FragmentResetBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigate(R.id.action_reset_to_login2)
                }
            })

        binding.signup.setOnClickListener {
            navController.navigate(R.id.action_reset_to_sign_up)
        }

        binding.oldAccount.setOnClickListener {
            navController.navigate(R.id.action_reset_to_sign_up)
        }

        binding.loginButton.setOnClickListener {
            performPasswordReset()        }
        }

    private fun performPasswordReset() {
        val email = binding.email.text.toString().trim()
        val newPassword = binding.password.text.toString().trim()
        val confirmPassword = binding.checkpassword.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email may not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Password fields may not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isPasswordValid(newPassword)) {
            Toast.makeText(requireContext(), "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character", Toast.LENGTH_LONG).show()
            return
        }

        val passResetRequest = PassResetRequest(
            confirm_password = confirmPassword,
            email = email,
            new_password = newPassword
        )


        AuthInstance.api.passreset(passResetRequest).enqueue(object : Callback<PassResetResponse> {
            override fun onResponse(call: Call<PassResetResponse>, response: Response<PassResetResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Password reset successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_reset_to_login2)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Error: Invalid request"
                        else -> "Error: ${response.message()}"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PassResetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
