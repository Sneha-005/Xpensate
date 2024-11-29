package com.example.xpensate.Fragments.Dashboard

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.example.xpensate.API.home.lineGraph
import com.example.xpensate.Adapters.LabelAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.Adapters.RecordAdapter
import com.example.xpensate.Modals.LabelItem
import com.example.xpensate.databinding.FragmentRecordsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Records : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!
    private lateinit var barChart: BarChart
    private var adapter: LabelAdapter? = null

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

        val recordAdapter = RecordAdapter(mutableListOf())
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
        loadBarChart()
        loadLineChart()
        fetchRecordsData(recordAdapter)
        heatmap()
    }

    fun heatmap() {
        val heatmapGrid = binding.heatmapGrid
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = AuthInstance.api.getLineGraphData().execute()
                if (response.isSuccessful) {
                    val lineGraphData = response.body()

                    if (lineGraphData != null) {
                        val expensesByDay = lineGraphData.expenses_by_day
                        val maxExpense = expensesByDay.maxOfOrNull { it.total } ?: 1.0

                        withContext(Dispatchers.Main) {
                            heatmapGrid.removeAllViews()

                            expensesByDay.forEach { day ->
                                val cellView = View(requireContext())
                                val color = getHeatMapColor(day.total.toInt(), maxExpense.toInt())

                                cellView.setBackgroundColor(color)

                                val layoutParams = GridLayout.LayoutParams().apply {
                                    width = 70
                                    height = 70
                                    leftMargin = 8
                                    rightMargin = 8
                                    topMargin = 8
                                    bottomMargin = 8
                                }

                                cellView.background = GradientDrawable().apply {
                                    shape = GradientDrawable.RECTANGLE
                                    cornerRadius = 16f
                                    setColor(color)
                                }

                                cellView.layoutParams = layoutParams

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
       CoroutineScope(Dispatchers.IO).launch {
           try {
               val response = AuthInstance.api.expenselist().execute()
               if (response.isSuccessful) {
                   val recordsResponse = response.body()

                   if (recordsResponse != null && recordsResponse.expenses.isNotEmpty()) {
                       val recordsList = recordsResponse.expenses

                       withContext(Dispatchers.Main) {
                           adapter.updateRecords(recordsList)
                           val totalExpense = recordsResponse.total_expense
                           binding.totalExpenseTextView.text = "₹$totalExpense"
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

    fun getHeatMapColor(value: Int, max: Int): Int {
        val percentage = value.toFloat() / max
        return when {
            percentage > 0.75 -> Color.parseColor("#004D00")
            percentage > 0.5 -> Color.parseColor("#006600")
            percentage > 0.25 -> Color.parseColor("#009900")
            else -> Color.parseColor("#00CC00")
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

                            categoryChartData.expenses_by_category.forEach { category ->
                                pieEntries.add(PieEntry(category.total.toFloat(), category.category))
                                totalSpent += category.total.toFloat()
                            }

                            val pieDataSet = PieDataSet(pieEntries, "")
                            val randomColors = categoryChartData.expenses_by_category.map { getRandomColor() }
                            pieDataSet.colors = randomColors

                            val pieData = PieData(pieDataSet)
                            pieData.setDrawValues(false)

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

    private fun loadBarChart() {
        barChart = binding.barChart

        val entries = listOf(
            BarEntry(1f, 10f),
            BarEntry(2f, 20f),
            BarEntry(3f, 30f)
        )

        val labels = listOf("Label 1", "Label 2", "Label 3")

        val dataSet = BarDataSet(entries, "Sample Data")
        dataSet.colors = listOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE
        )

        val data = BarData(dataSet)
        data.barWidth = 0.4f

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val leftAxis = barChart.axisLeft
        leftAxis.setDrawAxisLine(false)

        barChart.axisRight.isEnabled = false

        barChart.description.isEnabled = false

        barChart.data = data
        barChart.setFitBars(true)
        barChart.invalidate()
    }

    private fun loadLineChart() {
        val lineChart = binding.lineChart

        val entries1 = listOf(
            Entry(0f, 10f),
            Entry(1f, 15f),
            Entry(2f, 12f),
            Entry(3f, 18f),
            Entry(4f, 20f)
        )

        val entries2 = listOf(
            Entry(0f, 8f),
            Entry(1f, 10f),
            Entry(2f, 16f),
            Entry(3f, 12f),
            Entry(4f, 22f)
        )

        val dataSet1 = LineDataSet(entries1, "Line 1")
        dataSet1.color = Color.BLUE
        dataSet1.valueTextColor = Color.BLUE
        dataSet1.lineWidth = 2f

        val dataSet2 = LineDataSet(entries2, "Line 2")
        dataSet2.color = Color.RED
        dataSet2.valueTextColor = Color.RED
        dataSet2.lineWidth = 2f

        val lineData = LineData(dataSet1, dataSet2)

        lineChart.description.isEnabled = false
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

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

