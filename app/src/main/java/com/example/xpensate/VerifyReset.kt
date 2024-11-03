package com.example.xpensate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.databinding.FragmentVerifyBinding
import com.example.xpensate.network.AuthInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.CountDownTimer

class VerifyReset : Fragment() {
    private var _binding: FragmentVerifyBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var countDownTimer: CountDownTimer
    private val otpTimeout = 300000L

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
            otpDigit4.addTextChangedListener(OtpTextWatcher(otpDigit4, null, otpDigit3))
        }
        val email = arguments?.getString("email") ?: "default@example.com"
        binding.verifyButton.setOnClickListener {
            val otp = otpDigit1.text.toString() + otpDigit2.text.toString() + otpDigit3.text.toString() + otpDigit4.text.toString()
            if (otp.length == 4) {
                verifyOtp(email, otp)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
        }
        startOtpTimer(email)
    }

    private fun startOtpTimer(email: String) {
        countDownTimer = object : CountDownTimer(otpTimeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                binding.timerTextView.text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)
            }

            override fun onFinish() {
                binding.timerTextView.text = "Time expired!"
                binding.verifyButton.isEnabled = false
                resendOtp(email)
            }
        }.start()
    }

    private fun resendOtp(email: String) {
        val verifyRequest = VerifyRequest(email, "placeholder")

        AuthInstance.api.verify(verifyRequest).enqueue(object : Callback<VerifyResponse> {
            override fun onResponse(call: Call<VerifyResponse>, response: Response<VerifyResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Resent!", Toast.LENGTH_SHORT).show()
                    startOtpTimer(email)
                    binding.verifyButton.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOtp(email: String, otp: String) {
        val passResetRequest = PassResetRequest(
            confirm_password = "Sky@12345",
            email = email,
            new_password = "Sky@12345"
        )

        AuthInstance.api.passreset(passResetRequest).enqueue(object : Callback<PassResetResponse> {
            override fun onResponse(call: Call<PassResetResponse>, response: Response<PassResetResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.messsage?.firstOrNull() ?: "Password changed successfully"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    navController.navigate(R.id.action_verifyReset_to_reset)
                } else {
                    response.errorBody()?.let {
                        Toast.makeText(requireContext(), "Error: ${it.string()}", Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(requireContext(), "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PassResetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
