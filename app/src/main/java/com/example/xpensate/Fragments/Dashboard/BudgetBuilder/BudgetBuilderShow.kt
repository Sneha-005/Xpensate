package com.example.xpensate.Fragments.Dashboard.BudgetBuilder

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.xpensate.API.BudgetBuilder.BudgetExpensesResponse
import com.example.xpensate.API.BudgetBuilder.BudgetbuildShowResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.ProgressDialogHelper
import com.example.xpensate.R
import com.example.xpensate.TokenDataStore
import com.example.xpensate.databinding.FragmentBudgetBuilderShowBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetBuilderShow : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentBudgetBuilderShowBinding? = null
    private val binding get() = _binding!!
    private var needsData: Float = 0.0f
    private var luxuryData: Float = 0.0f
    private var savingsData: Float = 0.0f
    private var currencyRate: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBuilderShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            currencyRate = TokenDataStore.getCurrencyRate(requireContext()).firstOrNull() ?: 1.0

        }
        navController = findNavController()
        binding.edit.setOnClickListener {
            navController.navigate(R.id.action_budgetBuilderShow_to_budgetBuilder)
        }

        fetchExpenseData()
        fetchBudgetData()
    }

    private fun fetchBudgetData() {
        AuthInstance.api.getBudgetLimit().enqueue(object : Callback<BudgetbuildShowResponse> {
            override fun onResponse(
                call: Call<BudgetbuildShowResponse>,
                response: Response<BudgetbuildShowResponse>
            ) {
                if (response.isSuccessful) {
                    ProgressDialogHelper.hideProgressDialog()

                    val data = response.body()
                    if (data != null && data.success) {
                        val monthly = data.monthly * currencyRate
                        if(isAdded) {
                            updateUI(
                                "%.2f".format(monthly),
                                data.data.need,
                                data.data.luxury,
                                data.data.savings
                            )
                        }
                    } else {
                        Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BudgetbuildShowResponse>, t: Throwable) {
                ProgressDialogHelper.hideProgressDialog()

                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(monthly: String, need: Int, luxury: Int, savings: Int) {
        binding.amount.text = "$monthly"
        binding.needsText.text = "$need%"
        binding.luxuryText.text = "$luxury%"
        binding.savingText.text = "$savings%"

        fun calculateAmount(percentage: Int): Float =
            (monthly.toFloat() * percentage * currencyRate / 100).toFloat()

        val needAmount = calculateAmount(need)
        val luxuryAmount = calculateAmount(luxury)
        val savingAmount = calculateAmount(savings)

        binding.needAmount.text = "%.2f".format(needAmount)
        binding.luxuryAmount.text = "%.2f".format(luxuryAmount)
        binding.savingAmount.text = "%.2f".format(savingAmount)

        val needs = Pair(needAmount.toFloat(), needsData)
        val luxury = Pair(luxuryAmount.toFloat(), luxuryData)
        val savings = Pair(savingAmount.toFloat(), savingsData)

        setupBarChart(needs, luxury, savings)
    }
    private fun setupBarChart(
        needsData: Pair<Float, Float>,
        luxuryData: Pair<Float, Float>,
        savingsData: Pair<Float, Float>
    ) {
        fetchExpenseData()
        val barChart = binding.barChart

        val incomeEntries = listOf(
            BarEntry(0f, needsData.first),
            BarEntry(1f, luxuryData.first),
            BarEntry(2f, savingsData.first)
        )

        val expenseEntries = listOf(
            BarEntry(0f, needsData.second),
            BarEntry(1f, luxuryData.second),
            BarEntry(2f, savingsData.second)
        )

        val incomeDataSet = BarDataSet(incomeEntries, "").apply {
            color = Color.parseColor("#2BFDA5")
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            setDrawValues(false)
        }

        val expenseDataSet = BarDataSet(expenseEntries, "").apply {
            color = Color.parseColor("#067548")
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            setDrawValues(false)
        }

        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.2f
        }

        val allEntries = incomeEntries + expenseEntries
        val maxValue = allEntries.maxOf { it.y }

        barChart.apply {
            data = barData
            description.isEnabled = false
            setDrawGridBackground(false)
            setFitBars(true)

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = maxValue * 1.2f
                textColor = Color.WHITE
            }
            axisRight.isEnabled = false

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(listOf("Needs", "Luxury", "Savings"))
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                textColor = Color.WHITE
            }
            val leftAxis = barChart.axisLeft
            leftAxis.setDrawAxisLine(false)
            leftAxis.textColor = Color.WHITE
            leftAxis.granularity = 4000f
            leftAxis.axisMinimum = 0f
            leftAxis.axisMaximum = maxValue * 1.2f
            leftAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "${(value / 1000).toInt()}K"
                }
            }
            legend.apply {
                textColor = Color.WHITE
            }

            animateY(1000)

            val groupSpace = 0.2f
            val barSpace = 0.01f
            val barWidth = 0.35f
            xAxis.axisMinimum = -0.5f
            xAxis.axisMaximum = barData.getGroupWidth(groupSpace, barSpace) * 3
            groupBars(0f, groupSpace, barSpace)
        }

        barChart.invalidate()
    }

    private fun fetchExpenseData() {
        AuthInstance.api.getBudgetExpenses().enqueue(object : Callback<BudgetExpensesResponse> {
            override fun onResponse(
                call: Call<BudgetExpensesResponse>,
                response: Response<BudgetExpensesResponse>
            ) {
                if (response.isSuccessful) {
                    ProgressDialogHelper.hideProgressDialog()

                    val data = response.body()
                    if (data != null && data.success) {
                        needsData = data.needs_debit_total.toFloat()
                        luxuryData = data.luxury_debit_total.toFloat()

                        savingsData = data.monthly.toFloat()

                    } else {
                        Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BudgetExpensesResponse>, t: Throwable) {
                ProgressDialogHelper.hideProgressDialog()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        })
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
