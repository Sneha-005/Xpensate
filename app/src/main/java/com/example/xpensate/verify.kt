package com.example.xpensate

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentVerifyBinding

class verify : Fragment() {
    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to the splash screen
                navController.navigate(R.id.action_verify_to_sign_up)
            }
        })

        val otpDigit1 = view.findViewById<EditText>(R.id.otp_box1)
        val otpDigit2 = view.findViewById<EditText>(R.id.otp_box2)
        val otpDigit3 = view.findViewById<EditText>(R.id.otp_box3)
        val otpDigit4 = view.findViewById<EditText>(R.id.otp_box4)


        with(binding) {
            otpDigit1.addTextChangedListener(OtpTextWatcher(otpDigit1, otpDigit2, null))
            otpDigit2.addTextChangedListener(OtpTextWatcher(otpDigit2, otpDigit3, otpDigit1))
            otpDigit3.addTextChangedListener(OtpTextWatcher(otpDigit3, otpDigit4, otpDigit2))
            otpDigit4.addTextChangedListener(OtpTextWatcher(otpDigit4, null, otpDigit3)) // Last field
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class OtpTextWatcher(
    private val currentView: EditText,
    private val nextView: EditText?,
    private val previousView: EditText?
) : TextWatcher {

    init {
        // Detect backspace key press
        currentView.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && currentView.text.isEmpty()) {
                previousView?.requestFocus()
                true
            } else {
                false
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (!s.isNullOrEmpty()) {
            nextView?.requestFocus()
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}

