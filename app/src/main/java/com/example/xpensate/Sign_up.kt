package com.example.xpensate

import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.xpensate.databinding.FragmentSignUpBinding

class Sign_up : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to the splash screen
                navController.navigate(R.id.action_sign_up_to_splashScreen)
            }
        })

        // Set password fields to hidden by default
        binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.passwordVisibility.setImageResource(R.drawable.eye_closed)

        binding.checkpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.checkpasswordVisibility.setImageResource(R.drawable.eye_closed)

        // Password toggle functionality
        binding.passwordVisibility.setOnClickListener {
            togglePasswordVisibility()
        }

        binding.checkpasswordVisibility.setOnClickListener {
            toggleConfirmPasswordVisibility()
        }

        // Set up signup button with validation
        binding.signup.setOnClickListener {
            if (validateInput()) {
                navController.navigate(R.id.action_sign_up_to_verify)
            }
        }

        binding.oldAccount.setOnClickListener {
            navController.navigate(R.id.action_sign_up_to_login2)
        }

        binding.login.setOnClickListener {
            navController.navigate(R.id.action_sign_up_to_login2)
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordVisibility.setImageResource(R.drawable.heroicons_eye_solid) // Open-eye drawable
        } else {
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordVisibility.setImageResource(R.drawable.eye_closed) // Closed-eye drawable
        }
        binding.password.setSelection(binding.password.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        if (isConfirmPasswordVisible) {
            binding.checkpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.checkpasswordVisibility.setImageResource(R.drawable.heroicons_eye_solid) // Open-eye drawable
        } else {
            binding.checkpassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.checkpasswordVisibility.setImageResource(R.drawable.eye_closed) // Closed-eye drawable
        }
        binding.checkpassword.setSelection(binding.checkpassword.text.length)
    }

    private fun validateInput(): Boolean {
        // Define regex patterns for validation
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,15}$")

        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val confirmPassword = binding.checkpassword.text.toString().trim()

        // Validate email
        if (email.isEmpty() || !emailRegex.matches(email)) {
            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate password
        if (password.isEmpty() || !passwordRegex.matches(password)) {
            Toast.makeText(context, "Password must be between 8 and 15 characters, and include at least one uppercase letter, one lowercase letter, one digit, and one special character.", Toast.LENGTH_LONG).show()
            return false
        }

        // Check if passwords match
        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true // All validations passed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
