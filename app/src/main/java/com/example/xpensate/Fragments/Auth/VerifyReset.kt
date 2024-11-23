package com.example.xpensate.Fragments.Auth

import android.graphics.Color
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
import com.example.xpensate.AuthInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.CountDownTimer
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.xpensate.API.auth.request.ForgetPassRequest
import com.example.xpensate.API.auth.response.ForgetPassResponse
import com.example.xpensate.R
import com.example.xpensate.API.auth.request.VerifyResetRequest
import com.example.xpensate.API.auth.response.VerifyResetResponse
import com.example.xpensate.databinding.FragmentVerifyResetBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyReset : Fragment() {
    private var _binding: FragmentVerifyResetBinding? = null
    private val binding get() = _binding!!
    private var email: String? = null
    private lateinit var navController: NavController
    private lateinit var countDownTimer: CountDownTimer
    private val otpTimeout = 300000L
    private var verifyResetJob: Job? = null
    private var loadingDialog: AlertDialog? = null

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
            verifyResetJob?.cancel()
            verifyResetJob = lifecycleScope.launch {
                delay(500)
                val otp =
                    otpDigit1.text.toString() + otpDigit2.text.toString() + otpDigit3.text.toString() + otpDigit4.text.toString()
                if (otp.length == 4) {
                    email?.let { verifyOtp(it, otp) }
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid OTP", Toast.LENGTH_SHORT)
                        .show()
                }
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
                binding.timerTextView.apply{
                    text = "Time expired!"
                    setTextColor(Color.RED)
                }
                binding.verifyButton.isEnabled = false
                binding.resend.visibility=View.VISIBLE
            }
        }.start()
    }

    private fun resendOtp(email: String) {
        showLoadingDialog()
        val forgetPassRequest = ForgetPassRequest(email)

        AuthInstance.api.passforget(forgetPassRequest).enqueue(object : Callback<ForgetPassResponse> {
            override fun onResponse(call: Call<ForgetPassResponse>, response: Response<ForgetPassResponse>) {
                dismissLoadingDialog()
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "OTP Resent!", Toast.LENGTH_SHORT).show()
                    startOtpTimer(email)
                    binding.verifyButton.isEnabled = true
                } else {
                    Toast.makeText(requireContext(), "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ForgetPassResponse>, t: Throwable) {
                dismissLoadingDialog()
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOtp(email: String, otp: String) {
        showLoadingDialog()
       val verifyResetRequest= VerifyResetRequest(email, otp)
        AuthInstance.api.otpverify(verifyResetRequest).enqueue(object : Callback<VerifyResetResponse> {
            override fun onResponse(call: Call<VerifyResetResponse>, response: Response<VerifyResetResponse>) {
               dismissLoadingDialog()
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
                dismissLoadingDialog()
                Toast.makeText(requireContext(), "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoadingDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_loading, null)
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer.cancel()
        _binding = null
    }
}
