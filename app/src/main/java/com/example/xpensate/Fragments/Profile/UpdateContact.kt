package com.example.xpensate.Fragments.Profile

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.API.home.UpdateContact.OtpVerifyRequest
import com.example.xpensate.API.home.UpdateContact.UpdateContactOtpVerify
import com.example.xpensate.API.home.UpdateContact.UpdateContactResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Auth.OtpTextWatcher
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentUpdateContactBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateContact : Fragment() {
    private var _binding: FragmentUpdateContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var countDownTimer: CountDownTimer
    private val otpTimeout = 300000L
    private var verifyJob: Job? = null
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateContactBinding.inflate(inflater, container, false)
        return binding.root    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        var otpLayout = binding.otpLayout
        var phone = binding.phoneNo.text.toString()
        val updateButton = view.findViewById<View>(R.id.OtpButton)
        otpLayout?.visibility = View.GONE

        updateButton.setOnClickListener {
            phone = binding.phoneNo.text.toString().trim()
            if (phone.isNotEmpty()) {
                sendOtp(phone, otpLayout)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            }
        }

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

        binding.VerifyButton.setOnClickListener {
            val otp = otpDigit1.text.toString() + otpDigit2.text.toString() + otpDigit3.text.toString() + otpDigit4.text.toString()
            if (otp.length == 4) {
                verifyOtp(phone,otp)
            } else {
                Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT).show()
            }
        }
        binding.timerTextView.setOnClickListener {
            verifyJob?.cancel()
            verifyJob = lifecycleScope.launch {
                delay(500)
                resendOtp(phone)
            }
        }
        startOtpTimer(phone)
    }

    private fun startOtpTimer(phone: String) {
        countDownTimer = object : CountDownTimer(otpTimeout, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (_binding == null) return
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                binding.timerTextView.text = String.format("%02d:%02d", secondsRemaining / 60, secondsRemaining % 60)
            }
            override fun onFinish() {
                if (_binding == null) return
                binding.timerTextView.apply {
                    text = "Time expired!"
                    setTextColor(Color.RED)
                }
                binding.VerifyButton.isEnabled = false
                binding.timerTextView.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun resendOtp(phone: String) {
        AuthInstance.api.updateContact(phone).enqueue(object : Callback<UpdateContactResponse> {
            override fun onResponse(call: Call<UpdateContactResponse>, response: Response<UpdateContactResponse>) {
                if (_binding == null) return
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Resent!", Toast.LENGTH_SHORT).show()
                    startOtpTimer(phone)
                    binding.VerifyButton.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateContactResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendOtp(phone: String,otpLayout:View?=null) {
        AuthInstance.api.updateContact(phone).enqueue(object : Callback<UpdateContactResponse> {
            override fun onResponse(call: Call<UpdateContactResponse>, response: Response<UpdateContactResponse>) {
                try {
                    Log.d("sendOtp", "${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("sendOtp", "Response body: $responseBody")
                        Toast.makeText(context, "OTP sent successfully!", Toast.LENGTH_SHORT).show()
                        otpLayout?.visibility = View.VISIBLE
                        startOtpTimer(phone)
                    }
                }catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onFailure(call: Call<UpdateContactResponse>, t: Throwable) {
                Toast.makeText(context, "Error: Failed to send OTP", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun verifyOtp(contact: String, otp: String) {
        val request = OtpVerifyRequest(contact, otp)
        Log.d("verifyOtp", "Request: $request")
        AuthInstance.api.updateVerify(request).enqueue(object : Callback<UpdateContactOtpVerify> {
            override fun onResponse(call: Call<UpdateContactOtpVerify>, response: Response<UpdateContactOtpVerify>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Verified", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_updateContact_to_profile2)
                } else {
                    response.errorBody()?.let {
                        Log.e("verifyOtp", "Error: ${it.string()}")
                        Toast.makeText(requireContext(), "Error: ${it.string()}", Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(requireContext(), "OTP Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateContactOtpVerify>, t: Throwable) {
                Log.e("verifyOtp", "Network Error: ${t.message}")
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}