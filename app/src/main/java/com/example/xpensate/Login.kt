package com.example.xpensate

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentLoginBinding
import com.example.xpensate.network.AuthInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false

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

        // Handle back button press to navigate to splash screen
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isAdded) { // Ensure fragment is added before navigating
                    navController.navigate(R.id.action_login2_to_splashScreen)
                }
            }
        })

        lifecycleScope.launch {
            TokenDataStore.getAccessToken(requireContext()).collect { accessToken ->
                if (accessToken != null && isAdded) {
                    navController.navigate(R.id.action_login2_to_splashScreen)
                }
            }
        }

        setupUI(navController)
    }

    private fun setupUI(navController: NavController) {
        binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        binding.passwordVisibility.setImageResource(R.drawable.eye_closed)

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
            performForgotPassword()
        }

        binding.loginButton.setOnClickListener {
            if (validateInput()) {
                performLogin()
            }
        }
    }

    private fun performLogin() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()
        val loginRequest = LoginRequest(email, password)

        AuthInstance.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        saveTokens(loginResponse.tokens.access, loginResponse.tokens.refresh)
                        Toast.makeText(requireContext(), loginResponse.message ?: "Login successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_login2_to_splashScreen)
                    }
                } else {
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.errorMessage.text = "Error: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.errorMessage.visibility = View.VISIBLE
                binding.errorMessage.text = "Network error: ${t.message}"
            }
        })
    }

    private fun saveTokens(accessToken: String, refreshToken: String) {
        lifecycleScope.launch {
            TokenDataStore.saveTokens(requireContext(), accessToken, refreshToken)
        }
    }

    private fun performForgotPassword() {
        val email = binding.email.text.toString().trim()

        binding.errorMessage.visibility = View.GONE

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Email may not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isEmailValid(email)) {
            Toast.makeText(requireContext(), "Enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        val forgetPassRequest = ForgetPassRequest(email)

        AuthInstance.api.passforget(forgetPassRequest).enqueue(object : Callback<ForgetPassResponse> {
            override fun onResponse(call: Call<ForgetPassResponse>, response: Response<ForgetPassResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), response.body()?.message ?: "OTP sent on mail", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_login2_to_verifyReset)

                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Error: Invalid email"
                        else -> "Error: ${response.message()}"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgetPassResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        return emailRegex.matches(email)
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        if (isPasswordVisible) {
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.passwordVisibility.setImageResource(R.drawable.heroicons_eye_solid)
        } else {
            binding.password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            binding.passwordVisibility.setImageResource(R.drawable.eye_closed)
        }
        binding.password.setSelection(binding.password.text?.length ?: 0)
    }

    private fun validateInput(): Boolean {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        val emailRegex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")

        if (email.isEmpty() || !emailRegex.matches(email)) {
            Toast.makeText(context, "Invalid email format", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your password.", Toast.LENGTH_SHORT).show()
            return false
        }


        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
