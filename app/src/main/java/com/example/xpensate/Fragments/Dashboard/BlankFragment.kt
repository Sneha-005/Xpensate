package com.example.xpensate.Fragments.Dashboard

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.API.home.lineGraph
import com.example.xpensate.Adapters.LabelAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.Adapters.RecordAdapter
import com.example.xpensate.Adapters.SplitBillAdapter
import com.example.xpensate.LoadingDialogFragment
import com.example.xpensate.Modals.LabelItem
import com.example.xpensate.R
import com.example.xpensate.databinding.FragmentBlankBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class BlankFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!
    private var adapter: LabelAdapter? = null
    private var loadingDialog: LoadingDialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        binding.budgetBuilder.setOnClickListener {
            navController.navigate(R.id.action_blankFragment_to_budgetBuilder)
        }
        binding.moreRecords.setOnClickListener {
            navController.navigate(R.id.action_blankFragment_to_records)
        }
        binding.moreSplits.setOnClickListener {
            navController.navigate(R.id.action_blankFragment_to_splitBillMore)
        }


        val pieChart = _binding?.pieChart
        val legendListView = _binding?.legendListView

        val splitAdapter = SplitBillAdapter(mutableListOf())
        val splitRecyclerView = binding.billSplitRecycler
        splitRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        splitRecyclerView.adapter = splitAdapter
        fetchSplitBills(splitAdapter)

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        binding.monthText.text = currentMonth

        val recordAdapter = RecordAdapter(mutableListOf())
        val recordRecyclerView = binding.recordContainer
        recordRecyclerView.layoutManager = LinearLayoutManager(context)
        recordRecyclerView.adapter = recordAdapter

        fetchRecordsData(recordAdapter)
        setLineChartData()

        if (pieChart != null && legendListView != null) {
            setupPieChart(pieChart)
            adapter = LabelAdapter(requireContext(), emptyList())
            legendListView.adapter = adapter
        }
        binding.moreRecords.setOnClickListener {
            navController.navigate(R.id.action_blankFragment_to_records)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    private fun fetchSplitBills(adapter: SplitBillAdapter) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = AuthInstance.api.getSplitGroups().execute()
                if (response.isSuccessful) {
                    val splitBillsResponse = response.body()
                    if (splitBillsResponse != null && splitBillsResponse.data.isNotEmpty()) {
                        val splitList = splitBillsResponse.data
                        withContext(Dispatchers.Main) {
                            adapter.updateSplits(splitList)
                        }
                    } else {
                        dismissLoadingDialog()
                        Log.d("API Response", "No records found or data is null")
                    }
                } else {
                    Log.e("API Error", "Response code: ${response.code()}")
                    Log.d("API Error","Error: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Exception: ${e.message}")
            }
        }
    }

    private fun fetchRecordsData(adapter: RecordAdapter) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = AuthInstance.api.expenselist().execute()
                if (response.isSuccessful) {
                    val recordsResponse = response.body()
                    if (recordsResponse != null && recordsResponse.expenses.isNotEmpty()) {
                        val recordsList = recordsResponse.expenses.take(4)
                        withContext(Dispatchers.Main) {
                            adapter.updateRecords(recordsList)
                        }
                    } else {
                        Log.d("API Response", "No records found")
                    }
                } else {
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Exception: ${e.message}")
            }
        }
    }

    private fun setLineChartData() {
        val lineChart = binding.lineChart

        val xAxis = lineChart.xAxis
        xAxis.axisMinimum = 0f   // Start from 0
        xAxis.axisMaximum = 30f  // End at 30
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f // Show a label for each day
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 12f
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)

        lineChart.axisLeft.isEnabled = false
        lineChart.axisRight.isEnabled = false


        viewLifecycleOwner.lifecycleScope.launch {
            AuthInstance.api.getLineGraphData().enqueue(object : Callback<lineGraph> {
                override fun onResponse(call: Call<lineGraph>, response: Response<lineGraph>) {
                    if (!isAdded || _binding == null) return
                    if (response.isSuccessful) {
                        val lineGraphData = response.body()
                        binding.balanceText.text = "₹${lineGraphData?.total_expenses ?: 0}"

                        if (lineGraphData != null && lineGraphData.expenses_by_day.isNotEmpty()) {
                            val lineEntries = ArrayList<Entry>()

                            val fullMonthData = mutableMapOf<Int, Float>().apply {
                                for (day in 0..30) {
                                    this[day] = 0f
                                }
                            }

                            // Fill in actual expenses for specific days
                            lineGraphData.expenses_by_day.forEach { expensesByDay ->
                                val day = expensesByDay.date.split("-").last().toInt() // Extract day (DD)
                                fullMonthData[day] = expensesByDay.total.toFloat()
                            }

                            // Create entries from fullMonthData
                            fullMonthData.toSortedMap().forEach { (day, totalExpense) ->
                                lineEntries.add(Entry(day.toFloat(), totalExpense))
                            }

                            // Custom X-Axis Formatter for Days
                            xAxis.valueFormatter = IndexAxisValueFormatter((0..30).map { it.toString() })

                            // Create dataset
                            val lineDataSet = LineDataSet(lineEntries, "Expenses by Day").apply {
                                color = Color.WHITE
                                setDrawFilled(true)
                                fillAlpha = 30
                                lineWidth = 4f
                                fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_white)
                                mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                                setDrawCircles(false)
                            }

                            // Apply data to chart
                            val data = LineData(lineDataSet).apply { setDrawValues(false) }
                            lineChart.data = data

                            // Final chart settings
                            lineChart.legend.isEnabled = false
                            lineChart.description.isEnabled = false
                            lineChart.invalidate()
                        } else {
                            Toast.makeText(requireContext(), "No data available to display", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<lineGraph>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    class IndexAxisValueFormatter(private val days: List<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in days.indices) String.format("%02d", days[index].toInt()) else ""
        }
    }

    private fun setupPieChart(pieChart: PieChart) {
        viewLifecycleOwner.lifecycleScope.launch {
            AuthInstance.api.expenseChart().enqueue(object : Callback<CategoryChartResponse> {
                override fun onResponse(call: Call<CategoryChartResponse>, response: Response<CategoryChartResponse>) {
                    if (!isAdded || _binding == null) return
                    if (response.isSuccessful) {
                        val categoryChartData = response.body()
                        if (categoryChartData != null && categoryChartData.expenses_by_category.isNotEmpty()) {
                            val pieEntries = ArrayList<PieEntry>()
                            var totalSpent = 0f

                            val threshold = 5f
                            var othersTotal = 0f
                            categoryChartData.expenses_by_category.forEach { category ->
                                if (category.total.toFloat() < threshold) {
                                    othersTotal += category.total.toFloat()
                                } else {
                                    pieEntries.add(PieEntry(category.total.toFloat(), category.category))
                                }
                            }
                            if (othersTotal > 0) {
                                pieEntries.add(PieEntry(othersTotal, "Others"))
                            }

                            val randomColors = categoryChartData.expenses_by_category.map { getRandomColor() }
                            val pieDataSet = PieDataSet(pieEntries, "").apply {
                                colors = randomColors
                                sliceSpace = 5f
                                selectionShift = 5f
                            }

                            val pieData = PieData(pieDataSet)
                            pieData.setDrawValues(false)
                            pieData.setValueFormatter(PercentFormatter(pieChart))
                            pieChart.setUsePercentValues(true)
                            with(pieChart) {
                                data = pieData
                                setHoleColor(Color.TRANSPARENT)
                                holeRadius = 70f
                                isRotationEnabled = false
                                setCenterText("₹$totalSpent")
                                setCenterTextColor(Color.WHITE)
                                setCenterTextSize(16f)
                                setExtraOffsets(0f, 0f, 10f, 10f)

                                description.isEnabled = false
                                legend.isEnabled = false
                                setDrawEntryLabels(false)
                                invalidate()
                            }

                            populateCustomLegend(categoryChartData, randomColors)
                        } else {
                            Toast.makeText(requireContext(), "No data available for the Pie chart", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to fetch data for the Pie chart", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CategoryChartResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun populateCustomLegend(categoryChartData: CategoryChartResponse, colors: List<Int>) {
        val labelItems = categoryChartData.expenses_by_category.mapIndexed { index, category ->
            LabelItem(label = category.category, color = colors[index])
        }

        adapter?.let {
            it.updateLegend(labelItems)
        }
    }

    private fun getRandomColor(): Int {
        val random = java.util.Random()
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    private fun showLoadingDialog() {
        loadingDialog = LoadingDialogFragment.newInstance()
        loadingDialog?.isCancelable = false
        loadingDialog?.show(childFragmentManager, "loading")
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DoubleDigitFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return String.format("%02d", value.toInt())
    }
}