package com.example.xpensate.Fragments.Dashboard

import android.os.Bundle
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
import com.example.xpensate.API.home.CurrencyConverter.CurrencyConvertAPI
import com.example.xpensate.AuthInstance
import com.example.xpensate.OnCurrencySelectedListener
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.SharedViewModel
import com.example.xpensate.databinding.FragmentCurrencyConverterBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyConverter : Fragment(), OnCurrencySelectedListener {

    private lateinit var navController: NavController
    private var _binding: FragmentCurrencyConverterBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var debounceJob: Job? = null
    private val debounceDelay = 500L
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

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

        binding.reverse.setOnClickListener {
            val from = binding.from.text
            val to = binding.to.text
            val fromSymbol = binding.fromSymbol.text
            val toSymbol = binding.toSymbol.text
            val amountFrom = binding.amountFrom.text
            val amountTo = binding.amountTo.text

            binding.from.text = to
            binding.to.text = from
            binding.toSymbol.text = fromSymbol
            binding.fromSymbol.text = toSymbol
            binding.amountFrom.setText(amountTo)
            binding.amountTo.setText(amountFrom)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val selectedCurrency = sharedViewModel.selectedCurrency.value
                if (selectedCurrency != null) {
                    val (shortForm, _, symbol) = selectedCurrency
                    if (sharedViewModel.isFromSelected.value == true) {
                        binding.fromSymbol.text = symbol
                        binding.from.text = shortForm
                    } else {
                        binding.toSymbol.text = symbol
                        binding.to.text = shortForm
                    }
                }
                navController.navigate(R.id.action_currencyConverter_to_profile2)
            }
        })

        binding.from.text = "JPY"
        binding.to.text = "INR"

        sharedViewModel.selectedCurrency.observe(viewLifecycleOwner) { selectedCurrency ->
            val (shortForm, _, symbol) = selectedCurrency
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
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                debounceJob?.cancel()
                debounceJob = coroutineScope.launch {
                    try {
                        delay(debounceDelay)
                        val fromCurrency = binding.from.text.toString()
                        val toCurrency = binding.to.text.toString()
                        val amountText = s?.toString()?.trim()

                        val amount = amountText?.toDoubleOrNull()
                        if (amount != null && amount > 0) {
                            fetchConversionRate(fromCurrency, toCurrency, amount)
                        } else {
                            binding.amountTo.setText("")
                        }
                    } catch (e: Exception) {
                        Log.e("CurrencyConverter", "Error during currency conversion: ${e.message}")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun fetchConversionRate(fromCurrency: String, toCurrency: String, amount: Double) {
        try {
            ProgressDialogHelper.showProgressDialog(requireContext())
            Log.d("CurrencyConverter", "From: $fromCurrency, To: $toCurrency, Amount: $amount")

            AuthInstance.api.currencyConvert(fromCurrency, toCurrency, amount.toString())
                .enqueue(object : Callback<CurrencyConvertAPI> {
                    override fun onResponse(
                        call: Call<CurrencyConvertAPI>,
                        response: Response<CurrencyConvertAPI>
                    ) {
                        ProgressDialogHelper.hideProgressDialog()
                        try {
                            if (isAdded && response.isSuccessful && response.body() != null) {
                                val result = response.body()!!
                                if (result.success) {
                                    binding.amountTo.setText(result.value.toString())
                                } else {
                                    Toast.makeText(requireContext(), "Failed to convert currency", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                if(response.code() == 500){
                                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                }
                                val errorBody = response.message().toString()
                                Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("CurrencyConverter", "Error processing response: ${e.message}")
                            Toast.makeText(requireContext(), "Error processing data", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<CurrencyConvertAPI>, t: Throwable) {
                        ProgressDialogHelper.hideProgressDialog()
                        Log.e("CurrencyConverter", "API call failed: ${t.message}")
                        if (isAdded) {
                            Toast.makeText(requireContext(), "Failed to connect to server", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } catch (e: Exception) {
            ProgressDialogHelper.hideProgressDialog()
            Log.e("CurrencyConverter", "Exception in API call: ${e.message}")
            Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
            _binding = null
            debounceJob?.cancel()
            coroutineScope.cancel()
        } catch (e: Exception) {
            Log.e("CurrencyConverter", "Error on destroy view: ${e.message}")
        }
    }
}
