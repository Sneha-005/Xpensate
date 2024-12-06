package com.example.xpensate.Fragments.Profile

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.xpensate.API.home.AppCurrency
import com.example.xpensate.API.home.CurrencyConverter.CurrencyConvertAPI
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Dashboard.CurrencyChoose
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.SharedViewModel
import com.example.xpensate.TokenDataStore
import com.example.xpensate.TokenDataStore.saveCurrencyRate
import com.example.xpensate.databinding.FragmentPreferredCurrencyBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreferredCurrency : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentPreferredCurrencyBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var selectedCurrency: Triple<String, String, String>? = null
    private var saveMenuItem: MenuItem? = null
    private lateinit var progressDialog: ProgressDialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tab_menu, menu)

        saveMenuItem = menu.findItem(R.id.action_save)
        val spannableTitle = SpannableString("Save")
        spannableTitle.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
            0, spannableTitle.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        saveMenuItem?.title = spannableTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                selectedCurrency?.let {
                    binding.country.text = it.second
                    binding.symbol.text = it.third
                    saveCurrencyToServer(it.first)
                    fetchConversionRate(amount = 1)
                } ?: Toast.makeText(requireContext(), "No currency selected", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveCurrencyToServer(currency: String) {
        ProgressDialogHelper.showProgressDialog(requireContext())
        saveMenuItem?.isEnabled = false
        lifecycleScope.launch {
            val token = getToken()
            if (token != null) {
                try {
                    AuthInstance.api.appCurrency(currency).enqueue(object : Callback<AppCurrency> {
                        override fun onResponse(
                            call: Call<AppCurrency>,
                            response: Response<AppCurrency>
                        ) {
                            ProgressDialogHelper.hideProgressDialog()
                            saveMenuItem?.isEnabled = true
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Currency saved successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                if(response.code() == 500){
                                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                }
                                val errorBody = response.message().toString()
                                Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<AppCurrency>, t: Throwable) {
                            ProgressDialogHelper.hideProgressDialog()
                            saveMenuItem?.isEnabled = true
                            Toast.makeText(requireContext(), "Can't convert the currency", Toast.LENGTH_SHORT).show()
                        }
                    })
                } catch (e: Exception) {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                finally {
                    ProgressDialogHelper.hideProgressDialog()
                }
            } else {
                ProgressDialogHelper.hideProgressDialog()
                saveMenuItem?.isEnabled = true
                Toast.makeText(requireContext(), "Error retrieving token", Toast.LENGTH_SHORT).show()

            }
        }
    }
    private fun fetchConversionRate(amount: Int) {
        lifecycleScope.launch {
            val fromCurrency =
                getSavedCurrency() ?: "INR"
            val toCurrency = selectedCurrency?.first

            if (toCurrency == null) {
                Toast.makeText(requireContext(), "Please select a currency", Toast.LENGTH_SHORT)
                    .show()
                return@launch
            }

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
                                        lifecycleScope.launch {
                                            saveCurrencyRate(requireContext(),result.value)
                                            saveCurrency(toCurrency)
                                        }

                                    } else {
                                        Toast.makeText(
                                            requireContext(),
                                            "Failed to convert currency",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    if(response.code() == 500){
                                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                    }
                                    val errorBody = response.message().toString()
                                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    "CurrencyConverter",
                                    "Error processing response: ${e.message}"
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Error processing data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<CurrencyConvertAPI>, t: Throwable) {
                            ProgressDialogHelper.hideProgressDialog()
                            Log.e("CurrencyConverter", "API call failed: ${t.message}")
                            if (isAdded) {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to connect to server",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            } catch (e: Exception) {
                ProgressDialogHelper.hideProgressDialog()
                Log.e("CurrencyConverter", "Exception in API call: ${e.message}")
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_LONG).show()
            }
        }
    }
        private suspend fun getSavedCurrency(): String? {
            return TokenDataStore.getPreferredCurrency(requireContext()).firstOrNull()
        }

        private suspend fun saveCurrency(currency: String) {
            TokenDataStore.savePreferredCurrency(requireContext(), currency)
        }

    private suspend fun getToken(): String? {
        return TokenDataStore.getAccessToken(requireContext()).firstOrNull()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreferredCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        sharedViewModel.selectedCurrency.observe(viewLifecycleOwner) { selectedCurrency ->
            this.selectedCurrency = selectedCurrency
            binding.symbol.text = selectedCurrency.third
            binding.country.text = selectedCurrency.second
        }

        binding.country.setOnClickListener {
            childFragmentManager.commit {
                replace(R.id.currency_container, CurrencyChoose())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
