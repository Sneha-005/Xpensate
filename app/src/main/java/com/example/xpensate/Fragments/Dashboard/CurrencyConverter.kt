package com.example.xpensate.Fragments.Dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.xpensate.API.home.CurrencyConvertAPI
import com.example.xpensate.AuthInstance
import com.example.xpensate.OnCurrencySelectedListener
import com.example.xpensate.R
import com.example.xpensate.SharedViewModel
import com.example.xpensate.databinding.FragmentCurrencyConverterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyConverter : Fragment(), OnCurrencySelectedListener {
    private lateinit var navController: NavController
    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!
    private val handler = Handler(Looper.getMainLooper())
    private val debounceDelay = 500L
    private val sharedViewModel: SharedViewModel by activityViewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrencyConverterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCurrencySelected(shortForm: String, heading: String, symbol: String) {
        if (sharedViewModel.isFromSelected.value == true) {
            binding.from.text = shortForm
        } else {
            binding.to.text = shortForm
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val selectedCurrency = sharedViewModel.selectedCurrency.value
                if (selectedCurrency != null) {
                    val (shortForm, heading, symbol) = selectedCurrency
                    if (sharedViewModel.isFromSelected.value == true) {
                        binding.fromSymbol.text = symbol
                        binding.from.text = shortForm
                    } else {
                        binding.toSymbol.text = symbol
                        binding.to.text = shortForm
                    }
                }
                navController.navigate(R.id.action_currencyConverter_to_blankFragment)
            }
        })

        binding.from.text = "JPY"
        binding.to.text = "INR"

        sharedViewModel.selectedCurrency.observe(viewLifecycleOwner) { selectedCurrency ->
            val (shortForm, heading, symbol) = selectedCurrency
            if (sharedViewModel.isFromSelected.value == true) {
                binding.fromSymbol.text = symbol
                binding.from.text = shortForm
            } else {
                binding.toSymbol.text = symbol
                binding.to.text = shortForm
            }
        }

        binding.from.setOnClickListener {
            sharedViewModel.setIsFromSelected(true)
            childFragmentManager.commit {
                replace(R.id.currency_container, CurrencyChoose())
            }
        }

        binding.to.setOnClickListener {
            sharedViewModel.setIsFromSelected(false)
            childFragmentManager.commit {
                replace(R.id.currency_container, CurrencyChoose())
            }
        }

        binding.amountFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)

                val conversionRunnable = Runnable {
                    val fromCurrency = binding.from.text.toString()
                    val toCurrency = binding.to.text.toString()
                    val amountText = charSequence?.toString()?.trim()

                    val amount = amountText?.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        fetchConversionRate(fromCurrency, toCurrency, amountText)
                    } else {
                        binding.amountTo.setText("")
                    }
                }

                handler.postDelayed(conversionRunnable, debounceDelay)
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun fetchConversionRate(fromCurrency: String, toCurrency: String, amount: String) {
        Log.d("CurrencyConverter", "From: $fromCurrency, To: $toCurrency, Amount: $amount")

        AuthInstance.api.currencyConvert(fromCurrency, toCurrency, amount)
            .enqueue(object : Callback<CurrencyConvertAPI> {
                override fun onResponse(
                    call: Call<CurrencyConvertAPI>,
                    response: Response<CurrencyConvertAPI>
                ) {
                    Log.d("CurrencyConverter", "Response code: ${response.code()}")
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        if (result.success) {
                            binding.amountTo.setText(result.value.toString())
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to convert currency",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Log.e("CurrencyConverter", "Error: ${response.message()}")
                        Toast.makeText(
                            requireContext(),
                            "Error: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CurrencyConvertAPI>, t: Throwable) {
                    Log.e("CurrencyConverter", "API call failed: ${t.message}")
                    Toast.makeText(requireContext(), "Issue in API calling: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }
}