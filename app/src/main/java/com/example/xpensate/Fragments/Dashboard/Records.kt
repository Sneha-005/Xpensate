package com.example.xpensate.Fragments.Dashboard

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.API.home.IncomevsExpense
import com.example.xpensate.Adapters.LabelAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.Adapters.RecordAdapter
import com.example.xpensate.Modals.LabelItem
import com.example.xpensate.databinding.FragmentRecordsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class Records : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var barChart: BarChart
    private var adapter: LabelAdapter? = null
    private var total_expenses: String = "0.0"
    private var savings: String = "0.0"
    private var Income: String ="0.0"
    private val calendar = Calendar.getInstance()
    private val startDate: String
    private val endDate: String
    init {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        val pieChart = _binding?.pieChart
        val legendListView = _binding?.legendContainer

        val recordAdapter = RecordAdapter(mutableListOf(),findNavController())
        binding.filterButton.setOnClickListener {
            fetchRecordsData(recordAdapter)
        }
        binding.recordContainer.layoutManager = LinearLayoutManager(context)
        binding.recordContainer.adapter = recordAdapter
        navController = Navigation.findNavController(view)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.navigate(R.id.action_records_to_blankFragment)
            }
        })
        if (pieChart != null && legendListView != null) {
            setupPieChart(pieChart)
            adapter = LabelAdapter(requireContext(), emptyList())
            legendListView.adapter = adapter
        }
        binding.EndDate.setOnClickListener {
            setupDatePicker(binding.EndDate)
        }
        binding.StartDate.setOnClickListener {
            setupDatePicker(binding.StartDate)
        }
        fetchRecordsData(recordAdapter)
        heatmap()

    }


    fun heatmap() {
        val heatmapGrid = binding.heatmapGrid
        val totalDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))


        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = AuthInstance.api.getLineGraphData(startDate,endDate).execute()
                if (response.isSuccessful) {
                    val lineGraphData = response.body()

                    if (lineGraphData != null) {
                        val expensesByDay = lineGraphData.expenses_by_day
                        val maxExpense = expensesByDay.maxOfOrNull { it.total } ?: 1.0

                        val dayToExpenseMap = mutableMapOf<Int, Double>()
                        expensesByDay.forEach { day ->
                            val dayOfMonth = day.date.split("-").last().toInt()
                            dayToExpenseMap[dayOfMonth] = day.total
                        }

                        withContext(Dispatchers.Main) {
                            heatmapGrid.removeAllViews()
                            heatmapGrid.columnCount = 10

                            for (day in 1..totalDays) {
                                val cellView = View(requireContext())
                                val expense = dayToExpenseMap[day] ?: 0.0
                                val color = if (expense > 0) getHeatMapColor(expense.toInt(), maxExpense.toInt()) else Color.LTGRAY

                                cellView.tag = day
                                cellView.background = GradientDrawable().apply {
                                    shape = GradientDrawable.RECTANGLE
                                    cornerRadius = 16f
                                    setColor(color)
                                }
                                cellView.layoutParams = GridLayout.LayoutParams().apply {
                                    width = 70
                                    height = 70
                                    setMargins(8, 8, 8, 8)
                                }

                                cellView.setOnClickListener {
                                    val message = if (expense > 0) {
                                        "Date: $day, Expense: ₹$expense"
                                    } else {
                                        "Date: $day, No expenses recorded"
                                    }
                                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                                }

                                heatmapGrid.addView(cellView)
                            }
                        }
                    } else {
                        Log.d("API Response", "No line graph data found")
                    }
                } else {
                    Log.e("API Error", "Response code: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Exception: ${e.message}")
            }
        }
    }


    private fun fetchRecordsData(adapter: RecordAdapter) {
        val startDate = binding.StartDate.text.toString()
        val endDate = binding.EndDate.text.toString()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = AuthInstance.api.expenselist(startDate, endDate).execute()
                if (response.isSuccessful) {
                    val recordsResponse = response.body()
                    if (recordsResponse != null && recordsResponse.expenses.isNotEmpty()) {
                        val recordsList = recordsResponse.expenses
                        val incomeEntries = mutableListOf<Entry>()
                        val expenseEntries = mutableListOf<Entry>()

                        recordsList.forEachIndexed { index, record ->
                            if (record.is_credit) {
                                incomeEntries.add(Entry(index.toFloat(), record.amount.toFloat()))
                            } else {
                                expenseEntries.add(Entry(index.toFloat(), record.amount.toFloat()))
                            }
                        }

                        withContext(Dispatchers.Main) {
                            adapter.updateRecords(recordsList)
                            val totalExpense = recordsResponse.total_expense
                            binding.totalExpenseTextView.text = "₹$totalExpense"

                            loadLineChart(incomeEntries, expenseEntries)
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
    private fun setupDatePicker(dateField: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            dateField.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }
    fun getHeatMapColor(value: Int, max: Int): Int {
        val percentage = value.toFloat() / max
        return when {
            percentage > 0.75 -> Color.parseColor("#07603D")
            percentage > 0.5 -> Color.parseColor("#009657")
            percentage > 0.25 -> Color.parseColor("#00DF82")
            else -> Color.parseColor("#70FFC5")
        }
    }

    private fun setupPieChart(pieChart: PieChart) {
        viewLifecycleOwner.lifecycleScope.launch {
            AuthInstance.api.expenseChart(startDate, endDate).enqueue(object : Callback<CategoryChartResponse> {
                override fun onResponse(call: Call<CategoryChartResponse>, response: Response<CategoryChartResponse>) {
                    if (!isAdded || _binding == null) return
                    if (response.isSuccessful) {
                        val categoryChartData = response.body()
                        if (categoryChartData != null && categoryChartData.expenses_by_category.isNotEmpty()) {
                            val pieEntries = ArrayList<PieEntry>()
                            val totalSpent = categoryChartData.total_expenses

                            categoryChartData.expenses_by_category.forEach { category ->
                                val proportion = category.total.toFloat() / totalSpent
                                pieEntries.add(PieEntry(proportion.toFloat()))
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
                            pieChart.setNoDataText("No data available")
                            pieChart.setNoDataTextColor(Color.WHITE)
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
                            total_expenses = "$totalSpent"
                            savings = (Income.toFloat() - total_expenses.toFloat()).toString()
                            loadBarChart()

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

    private fun loadBarChart() {
        barChart = binding.barChart
        val entries = mutableListOf<BarEntry>()
        viewLifecycleOwner.lifecycleScope.launch {
            AuthInstance.api.barChart().enqueue(object : Callback<IncomevsExpense> {
                override fun onResponse(
                    call: Call<IncomevsExpense>,
                    response: Response<IncomevsExpense>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            val totalIncome = it.total_income.toFloat()
                            val totalExpense = it.total_expense.toFloat()
                            val savings = totalIncome - totalExpense
                            entries.add(BarEntry(0f, totalIncome))
                            entries.add(BarEntry(1f, totalExpense))
                            entries.add(BarEntry(2f, savings))

                            val labels = listOf("Income", "Expenses", "Savings")
                            val colors = listOf(Color.BLUE, Color.RED, Color.GREEN)

                            val dataSet = BarDataSet(entries, "").apply {
                                setColors(colors)
                            }

                            val data = BarData(dataSet)
                            data.barWidth = 0.25f

                            val xAxis = barChart.xAxis
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.setDrawGridLines(false)
                            xAxis.setDrawAxisLine(true)
                            xAxis.granularity = 1f
                            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                            xAxis.textColor = Color.WHITE

                            val leftAxis = barChart.axisLeft
                            leftAxis.setDrawAxisLine(false)
                            leftAxis.textColor = Color.WHITE
                            leftAxis.granularity = 4000f
                            leftAxis.axisMinimum = 0f
                            leftAxis.axisMaximum = 20000f
                            leftAxis.valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    return "${(value / 1000).toInt()}K"
                                }
                            }

                            barChart.axisRight.isEnabled = false
                            barChart.description.isEnabled = false
                            barChart.data = data
                            barChart.setFitBars(true)
                            barChart.invalidate()
                        }
                    }
                }

                override fun onFailure(call: Call<IncomevsExpense>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadLineChart(incomeEntries: List<Entry>, expenseEntries: List<Entry>) {
        val lineChart = binding.lineChart

        val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
            color = Color.GREEN
            valueTextColor = Color.GREEN
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.GREEN)
            setDrawCircles(false)
            setDrawValues(false)
        }

        val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
            color = Color.RED
            valueTextColor = Color.RED
            lineWidth = 2f
            circleRadius = 4f
            setCircleColor(Color.RED)
            setDrawCircles(false)
            setDrawValues(false)
        }

        val lineData = LineData(incomeDataSet, expenseDataSet)

        val allValues = (incomeEntries + expenseEntries).map { it.y }
        val maxValue = allValues.maxOrNull() ?: 0f

        val axisMaximum = (maxValue / 10000).toInt() * 10000 + 10000
        val granularity = axisMaximum / 5f

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.setDrawLabels(false)
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.valueFormatter = IndexAxisValueFormatter(incomeEntries.mapIndexed { index, _ -> "Day ${index + 1}" })

        val leftAxis = lineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.textColor = Color.WHITE
        leftAxis.axisMinimum = 0f
        leftAxis.granularity = granularity
        leftAxis.axisMaximum = axisMaximum.toFloat()
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${(value / 1000).toInt()}K"
            }
        }
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        lineChart.axisRight.isEnabled = false

        lineChart.description.isEnabled = false
        lineChart.legend.textColor = Color.WHITE
        lineChart.legend.form = Legend.LegendForm.CIRCLE

        lineChart.data = lineData
        lineChart.invalidate()
    }


    private fun getRandomColor(): Int {
        val random = java.util.Random()
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

