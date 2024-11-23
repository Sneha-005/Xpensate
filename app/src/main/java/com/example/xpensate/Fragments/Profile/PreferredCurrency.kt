package com.example.xpensate.Fragments.Profile

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.example.xpensate.AuthInstance
import com.example.xpensate.Fragments.Dashboard.CurrencyChoose
import com.example.xpensate.R
import com.example.xpensate.SharedViewModel
import com.example.xpensate.TokenDataStore
import com.example.xpensate.databinding.FragmentPreferredCurrencyBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tab_menu, menu)

        val saveMenuItem = menu.findItem(R.id.action_save)
        val spannableTitle = SpannableString("Save")
        spannableTitle.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
            0, spannableTitle.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        saveMenuItem.title = spannableTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                selectedCurrency?.let {
                    binding.country.text = it.second
                    binding.symbol.text = it.third
                    saveCurrencyToServer(it.first)
                } ?: Toast.makeText(requireContext(), "No currency selected", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveCurrencyToServer(currency: String) {
        lifecycleScope.launch {
            val token = getToken()
            if (token != null) {
                try {
                    AuthInstance.api.appCurrency(currency).enqueue(object : Callback<AppCurrency> {
                        override fun onResponse(
                            call: Call<AppCurrency>,
                            response: Response<AppCurrency>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Currency saved successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val errorMessage =
                                    response.errorBody()?.string() ?: "Unknown error"
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to save currency: $errorMessage",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<AppCurrency>, t: Throwable) {
                            Toast.makeText(requireContext(), "Can't convert the currency", Toast.LENGTH_SHORT).show()
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Error retrieving token", Toast.LENGTH_SHORT).show()
            }
        }
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