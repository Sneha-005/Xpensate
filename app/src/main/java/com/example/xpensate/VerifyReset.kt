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
import com.example.xpensate.network.AuthInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.CountDownTimer
import android.util.Log
import com.example.xpensate.databinding.FragmentVerifyResetBinding

class VerifyReset : Fragment() {
    private var _binding: FragmentVerifyResetBinding? = null
    private val binding get() = _binding!!
    private var email: String? = null
    private lateinit var navController: NavController
    private lateinit var countDownTimer: CountDownTimer
    private val otpTimeout = 300000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVerifyResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        email = arguments?.getString("email")

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
        binding.verifyButton.setOnClickListener {
            val otp = otpDigit1.text.toString() + otpDigit2.text.toString() + otpDigit3.text.toString() + otpDigit4.text.toString()
            if (otp.length == 4) {
                email?.let { verifyOtp(it, otp) }
            } else {
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
        }
        binding.resend.setOnClickListener {
            email?.let { resendOtp(it) }
        }
        email?.let { startOtpTimer(it) }
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
                binding.resend.visibility=View.VISIBLE
            }
        }.start()
    }

    private fun resendOtp(email: String) {
        val verifyResetRequest = VerifyResetRequest(email, "placeholder")

        AuthInstance.api.otpverify(verifyResetRequest).enqueue(object : Callback<VerifyResetResponse> {
            override fun onResponse(call: Call<VerifyResetResponse>, response: Response<VerifyResetResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Resent!", Toast.LENGTH_SHORT).show()
                    startOtpTimer(email)
                    binding.verifyButton.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<VerifyResetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOtp(email: String, otp: String) {
       val verifyResetRequest=VerifyResetRequest(email, otp)
        AuthInstance.api.otpverify(verifyResetRequest).enqueue(object : Callback<VerifyResetResponse> {
            override fun onResponse(call: Call<VerifyResetResponse>, response: Response<VerifyResetResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "OTP Verified successfully!"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    try {
                        val action = VerifyResetDirections.actionVerifyResetToReset(email,otp)
                        navController.navigate(action)
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("LoginFragment", "Navigation error: ${e.message}", e)
                    }
                } else {
                    Toast.makeText(requireContext(), "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<VerifyResetResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
        _binding = null
    }
}
