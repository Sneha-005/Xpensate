package com.example.xpensate.Fragments.Dashboard.BudgetBuilder

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.xpensate.API.BudgetBuilder.BudgetExpensesResponse
import com.example.xpensate.API.BudgetBuilder.CreateBudgetResponse
import com.example.xpensate.API.BudgetBuilder.CreateMonthlyLimitResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.databinding.FragmentBudgetBuilderBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetBuilder : Fragment() {

    private var _binding: FragmentBudgetBuilderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetBuilderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val need = binding.needsText.text.toString().toDouble()
        val luxury = binding.LuxuryText.text.toString().toDouble()
        val savings = binding.savingText.text.toString().toDouble()
        val monthlyLimit = binding.amount.text.toString().toDouble()
        val needAmount = monthlyLimit * need / 100f
        val luxuryAmount = monthlyLimit * luxury / 100f
        val savingsAmount = monthlyLimit * savings / 100f

        binding.needAmount.text = needAmount.toString()
        binding.luxuryAmount.text = luxuryAmount.toString()
        binding.savingAmount.text = savingsAmount.toString()

        submitBudget(luxury, need, savings)
        submitMonthlyLimit(monthlyLimit)

        fetchBudgetExpenses(need, luxury, savings)
    }

    private fun fetchBudgetExpenses(need: Double, luxury: Double, savings: Double) {
        AuthInstance.api.getBudgetExpenses().enqueue(object :
            Callback<BudgetExpensesResponse> {
            override fun onResponse(
                call: Call<BudgetExpensesResponse>,
                response: Response<BudgetExpensesResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { budgetExpensesResponse ->
                     //   val expenses = budgetExpensesResponse.expenses.map { it.amount.toFloat() }
                      //  setupBarChart(need, luxury, savings, expenses)
                    }
                } else {

                }
            }

            override fun onFailure(call: Call<BudgetExpensesResponse>, t: Throwable) {
                Log.e("BudgetBuilder", "Error: ${t.message}")
            }
        })
    }

    private fun setupBarChart(
        need: Double,
        luxury: Double,
        savings: Double,
        expenses: List<Float>
    ) {
        val barChart = binding.barChart

        val incomeEntries = listOf(
            BarEntry(0f, need.toFloat()),
            BarEntry(1f, luxury.toFloat()),
            BarEntry(2f, savings.toFloat())
        )

        val expenseEntries = expenses.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value)
        }

        val incomeDataSet = BarDataSet(incomeEntries, "Income").apply {
            color = Color.parseColor("#00994C")
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val expenseDataSet = BarDataSet(expenseEntries, "Expense").apply {
            color = Color.parseColor("#00FF99")
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.2f
        }

        barChart.apply {
            data = barData
            description.isEnabled = false
            setDrawGridBackground(false)
            setFitBars(true)

            axisLeft.apply {
                axisMinimum = 0f
                textColor = Color.WHITE
            }
            axisRight.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(listOf("Needs", "Luxury", "Saving"))
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textColor = Color.WHITE
            }

            legend.apply {
                textColor = Color.WHITE
            }

            animateY(1000)

            val groupSpace = 0.4f
            val barSpace = 0.1f
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = barData.getGroupWidth(groupSpace, barSpace) * 3
            groupBars(0f, groupSpace, barSpace)
        }

        barChart.invalidate()
    }

    private fun submitBudget(luxury: Double, need: Double, savings: Double) {
        AuthInstance.api.createBudgetLimit(luxury, need, savings).enqueue(object :
            Callback<CreateBudgetResponse> {
            override fun onResponse(call: Call<CreateBudgetResponse>, response: Response<CreateBudgetResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Budget Created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Budget Creation Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateBudgetResponse>, t: Throwable) {
                Log.e("BudgetBuilder", "Error: ${t.message}")
            }
        })
    }

    private fun submitMonthlyLimit(monthlyLimit: Double) {
        AuthInstance.api.setMonthlyLimit(monthlyLimit).enqueue(object :
            Callback<CreateMonthlyLimitResponse> {
            override fun onResponse(
                call: Call<CreateMonthlyLimitResponse>,
                response: Response<CreateMonthlyLimitResponse>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Monthly Limit Set Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to Set Monthly Limit", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateMonthlyLimitResponse>, t: Throwable) {
                Log.e("BudgetBuilder", "Error: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
