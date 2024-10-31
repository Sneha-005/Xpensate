package com.example.xpensate

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentLoginBinding

class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false // By default, password is hidden

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to the splash screen
                navController.navigate(R.id.action_login2_to_splashScreen)
            }
        })

        // Set password field to hidden by default
        binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.passwordVisibility.setImageResource(R.drawable.eye_closed) // Set to closed-eye icon by default

        // Password toggle functionality
        binding.passwordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.signup.setOnClickListener {
            navController.navigate(R.id.action_login2_to_sign_up)
        }

        binding.oldAccount.setOnClickListener {
            navController.navigate(R.id.action_login2_to_sign_up)
        }

        binding.forgotPassword.setOnClickListener {
            navController.navigate(R.id.action_login2_to_reset)
        }

        // Add login button functionality to validate email and password
        binding.loginButton.setOnClickListener {
            if (validateInput()) {
                // If validation passes, proceed with the login process
                // Implement your login logic here (e.g., API request)
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_login2_to_splashScreen) // Navigate to the home screen
            }
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordVisibility.setImageResource(R.drawable.heroicons_eye_solid) // Replace with open-eye drawable
        } else {
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordVisibility.setImageResource(R.drawable.eye_closed) // Replace with closed-eye drawable
        }
        binding.password.setSelection(binding.password.text?.length ?: 0) // Keep cursor at end
    }

    private fun validateInput(): Boolean {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

        // Regex for validating password (at least 8 characters, at least one uppercase, one lowercase, one digit, one special character)
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,15}$")

        // Validate email
        if (email.isEmpty() || !emailRegex.matches(email)) {
            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your password.", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check password regex
        if (!passwordRegex.matches(password)) {
            Toast.makeText(requireContext(), "Password must be between 8 and 15 characters and include at least one uppercase letter, one lowercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show()
            return false
        }

        return true // All validations passed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
