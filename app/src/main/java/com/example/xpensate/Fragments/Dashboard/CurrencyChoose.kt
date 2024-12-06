package com.example.xpensate.Fragments.Dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.CurrencyConverter.CurrencyData
import com.example.xpensate.AuthInstance
import com.example.xpensate.Adapters.CurrencyAdapter
import com.example.xpensate.Modals.CurrencyClass
import com.example.xpensate.OnCurrencySelectedListener
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.SharedViewModel
import com.example.xpensate.databinding.FragmentCurrencyChooseBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CurrencyChoose : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentCurrencyChooseBinding? = null
    private val binding get() = _binding!!
    private lateinit var currencyClassInstance: CurrencyClass
    private var currencyList: List<List<String>> = listOf()
    private lateinit var adapter: CurrencyAdapter
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currencyClassInstance = CurrencyClass.getDefaultCurrencies()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentCurrencyChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.searchView.queryHint = "Search"

        binding.currencyContainer.layoutManager = LinearLayoutManager(requireContext())

        ProgressDialogHelper.showProgressDialog(requireContext())

        fetchCurrencyData()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun fetchCurrencyData() {
        AuthInstance.api.currencyData().enqueue(object : Callback<CurrencyData> {
            override fun onResponse(call: Call<CurrencyData>, response: Response<CurrencyData>) {
                ProgressDialogHelper.hideProgressDialog()

                if (response.isSuccessful) {
                    val currencyData = response.body()
                    if (currencyData != null && currencyData.success) {
                        currencyList = currencyData.data
                        if (_binding != null) {
                            adapter = CurrencyAdapter(currencyList, currencyClassInstance, object : OnCurrencySelectedListener {
                                override fun onCurrencySelected(shortForm: String, heading: String, symbol: String) {
                                    sharedViewModel.setSelectedCurrency(shortForm, heading, symbol)
                                }
                            })
                            binding.currencyContainer.adapter = adapter
                        }
                    } else {
                        Toast.makeText(binding.root.context, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CurrencyData>, t: Throwable) {
                ProgressDialogHelper.hideProgressDialog()
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
