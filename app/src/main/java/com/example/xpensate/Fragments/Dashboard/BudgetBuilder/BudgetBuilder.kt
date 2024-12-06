package com.example.xpensate.Fragments.Dashboard.BudgetBuilder

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.API.BudgetBuilder.BudgetExpensesResponse
import com.example.xpensate.API.BudgetBuilder.CreateBudgetResponse
import com.example.xpensate.API.BudgetBuilder.CreateMonthlyLimitResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.TokenDataStore
import com.example.xpensate.databinding.FragmentBudgetBuilderBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class BudgetBuilder : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentBudgetBuilderBinding? = null
    private val binding get() = _binding!!
    private var monthlyLimit = 0.0
    private var currencyRate: Double? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBuilderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        lifecycleScope.launch {
            currencyRate = TokenDataStore.getCurrencyRate(requireContext()).firstOrNull() ?: 1.0
        }

        binding.saveButton.setOnClickListener {
            if (isDataValid()) {
                navController.navigate(R.id.action_budgetBuilder_to_budgetBuilderShow)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        binding.saveButton.setOnClickListener {
            val needText = binding.needsText.text.toString()
            val luxuryText = binding.luxuryText.text.toString()
            val savingsText = binding.savingText.text.toString()

            if (isDataValid(needText, luxuryText, savingsText)) {
                val need = needText.toDouble()
                val luxury = luxuryText.toDouble()
                val savings = savingsText.toDouble()
                createBudgetLimit(need, luxury, savings)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }

        monthlyLimit = binding.amount.text.toString().toDoubleOrNull() ?: 0.0
        if (monthlyLimit > 0) {
            saveMonthlyLimit(monthlyLimit)
        } else {
            Toast.makeText(requireContext(), "Please set a valid monthly limit", Toast.LENGTH_SHORT).show()
        }

        if (!isDateAllowed()) {
            Toast.makeText(
                requireContext(),
                "You can only modify the budget on the 1st or 2nd of the month.",
                Toast.LENGTH_LONG
            ).show()
            binding.saveButton.isEnabled = false
            return
        }
    }

    private fun isDataValid(vararg fields: String): Boolean {
        return fields.all { it.isNotBlank() && it.toDoubleOrNull() != null }
    }

    private fun isDateAllowed(): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        return dayOfMonth == 1 || dayOfMonth == 2
    }

    private fun createBudgetLimit(need: Double, luxury: Double, savings: Double) {
        ProgressDialogHelper.showProgressDialog(requireContext())
        try {
            AuthInstance.api.createBudgetLimit(need, luxury, savings).enqueue(object : Callback<CreateBudgetResponse> {
                override fun onResponse(
                    call: Call<CreateBudgetResponse>,
                    response: Response<CreateBudgetResponse>
                ) {
                    if (isAdded) {
                        if (response.isSuccessful) {
                            ProgressDialogHelper.hideProgressDialog()
                            Toast.makeText(
                                requireContext(),
                                "Budget limit created successfully!",
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
                }

                override fun onFailure(call: Call<CreateBudgetResponse>, t: Throwable) {
                    ProgressDialogHelper.hideProgressDialog()
                    if(isAdded) {
                        Log.e("BudgetBuilder", "API Call failed: ${t.message}")
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("BudgetBuilder", "Error during API call: ${e.message}")
            Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveMonthlyLimit(monthlyLimit: Double) {
        try {
            AuthInstance.api.setMonthlyLimit(monthlyLimit).enqueue(object : Callback<CreateMonthlyLimitResponse> {
                override fun onResponse(
                    call: Call<CreateMonthlyLimitResponse>,
                    response: Response<CreateMonthlyLimitResponse>
                ) {
                    if(isAdded) {
                        try {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Monthly limit set successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.e("BudgetBuilder", "Error: ${response.errorBody()?.string()}")
                                if(response.code() == 500){
                                    Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                                }
                                val errorBody = response.message().toString()
                                Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                            }
                        }catch (e: Exception){
                            Toast.makeText(requireContext(),"Network error",Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CreateMonthlyLimitResponse>, t: Throwable) {
                    if(isAdded) {
                        Log.e("BudgetBuilder", "API Call failed: ${t.message}")
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong. Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("BudgetBuilder", "Error during API call: ${e.message}")
            Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
