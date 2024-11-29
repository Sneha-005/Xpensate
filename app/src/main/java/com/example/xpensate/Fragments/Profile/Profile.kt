package com.example.xpensate.Fragments.Profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.API.home.UpdateUsernameResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.TokenDataStore
import com.example.xpensate.TokenUtils
import com.example.xpensate.databinding.FragmentProfileBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class Profile : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val debounceHandler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null
    private val debounceDelay = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRealTimeNameUpdate()
        navController = findNavController()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_profile2_to_blankFragment)
            }
        })
        binding.userName.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                return@setOnKeyListener true
            }
            false
        }
        lifecycleScope.launch {
            val savedUsername = TokenDataStore.getUsername(requireContext()).first()
            if (!savedUsername.isNullOrBlank()) {
                binding.userName.setText(savedUsername)
                Log.d("ProfileFragment", "Loaded username: $savedUsername")
            }
        }
        binding.userName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                lifecycleScope.launch {
                    editable?.let {
                        TokenDataStore.saveUsername(requireContext(), it.toString())
                        Log.d("ProfileFragment", "Username saved: ${it.toString()}")
                    }
                }
            }
        })


        val card1: View = view.findViewById(R.id.card1)
        card1.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_updateContact)
        }

        val card2: View = view.findViewById(R.id.card2)
        card2.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_preferredCurrency)
        }

        val card3: View = view.findViewById(R.id.card3)
        card3.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_appLock)
        }

        val card4: View = view.findViewById(R.id.card4)
        card4.setOnClickListener {
            navController.navigate(R.id.action_profile2_to_currencyConverter)
        }
        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                TokenDataStore.clearTokens(requireContext())
                Toast.makeText(requireContext(),"Logged out Successfully",Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.action_profile2_to_login2)

            }
        }

        binding.userName.setOnClickListener {
            binding.userName.text?.let { it1 -> updateUserName(it1) }
        }

        binding.card2.logo.background = ContextCompat.getDrawable(requireContext(), R.drawable.currency_preference)
        binding.card2.textView.text = "Currency Preference"
        binding.card3.logo.background = ContextCompat.getDrawable(requireContext(), R.drawable.applock)
        binding.card3.textView.text = "App Lock"
        binding.card4.logo.background = ContextCompat.getDrawable(requireContext(), R.drawable.currency_nav)
        binding.card4.textView.text = "Exchanger"
        binding.card5.logo.background = ContextCompat.getDrawable(requireContext(), R.drawable.notification)
        binding.card5.textView.text = "Push Notifications"
        binding.card6.logo.background = ContextCompat.getDrawable(requireContext(), R.drawable.share)
        binding.card6.textView.text = "Share App"
        binding.card5.arrow.background= ContextCompat.getDrawable(requireContext(),R.drawable.switcher)
    }

    private fun setupRealTimeNameUpdate() {
        binding.userName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                val newName = editable?.toString()?.trim()
                val currentName = binding.userName.text.toString().trim()

                if (!newName.isNullOrEmpty() && newName != currentName) {
                    debounceRunnable?.let { debounceHandler.removeCallbacks(it) }
                    debounceRunnable = Runnable {
                        updateUserName(editable)
                    }
                    debounceHandler.postDelayed(debounceRunnable!!, debounceDelay)
                }
            }
        })
    }

    private fun updateUserName(newName: Editable) {
        AuthInstance.api.updateName(newName).enqueue(object : Callback<UpdateUsernameResponse> {
            override fun onResponse(call: Call<UpdateUsernameResponse>, response: retrofit2.Response<UpdateUsernameResponse>) {
                Log.d("UpdateUserName", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == "true") {
                        lifecycleScope.launch {
                            TokenDataStore.saveUsername(requireContext(), newName.toString())
                        }
                        Toast.makeText(requireContext(), "Name updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), responseBody?.message ?: "Error updating name", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "HTTP error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateUsernameResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}