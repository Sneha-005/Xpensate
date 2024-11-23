package com.example.xpensate.Fragments.Dashboard

import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import com.example.xpensate.R
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
import com.example.xpensate.Modals.LabelItem
import com.example.xpensate.databinding.FragmentBlankBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.PieChartRenderer
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
        setupSemiCirclePieChart(binding.semipieChart)

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
                    if (splitBillsResponse != null && splitBillsResponse.data != null && splitBillsResponse.data.isNotEmpty()) {
                        val splitList = splitBillsResponse.data
                        withContext(Dispatchers.Main) {
                            adapter.updateSplits(splitList)
                        }
                    } else {
                        Log.d("API Response", "No records found or data is null")
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
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = 30f
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 16f
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = DoubleDigitFormatter()

        val leftAxis = lineChart.axisLeft
        leftAxis.granularity = 10f
        leftAxis.textColor = Color.RED
        leftAxis.textSize = 12f
        leftAxis.setDrawGridLines(false)

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
                            lineGraphData.expenses_by_day.forEachIndexed { index, expensesByDay ->
                                val dateIndex = index.toFloat()
                                val totalExpense = expensesByDay.total.toFloat()
                                lineEntries.add(Entry(dateIndex, totalExpense))
                            }

                            val lineDataSet = LineDataSet(lineEntries, "Expenses by Day")
                            lineDataSet.color = Color.WHITE
                            lineDataSet.setDrawFilled(true)
                            lineDataSet.fillAlpha = 30
                            lineDataSet.lineWidth = 4f

                            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_white)
                            lineDataSet.fillDrawable = drawable

                            lineDataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                            lineDataSet.setDrawCircles(false)
                            lineChart.axisLeft.isEnabled = false
                            lineChart.axisRight.isEnabled = false
                            val data = LineData(lineDataSet)
                            lineChart.data = data
                            data.setDrawValues(false)
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

    private fun setupSemiCirclePieChart(semiPieChart: PieChart) {
        val semipieEntries = ArrayList<PieEntry>()
        semipieEntries.add(PieEntry(80f, "Category 1"))
        semipieEntries.add(PieEntry(20f, "Category 2"))

        val semipieDataSet = PieDataSet(semipieEntries, "")
        semipieDataSet.colors = listOf(Color.GREEN, Color.GRAY)

        val semipieData = PieData(semipieDataSet)
        semipieData.setDrawValues(false)

        with(semiPieChart) {
            setHoleColor(Color.GREEN)
            setTransparentCircleColor(Color.GREEN)
            setTransparentCircleAlpha(0)
            holeRadius = 90f
            transparentCircleRadius = 95f
            data = semipieData
            rotationAngle = 180f
            maxAngle = 180f
            description.isEnabled = false
            setDrawRoundedSlices(true)
            setDrawHoleEnabled(true)
            legend.isEnabled = false
            isRotationEnabled = false
            setCenterText("Spent of ₹0")
            setDrawEntryLabels(false)
            setDrawMarkers(false)
            setExtraOffsets(0f, -10f, 0f, -10f)

            val vectorDrawable = ContextCompat.getDrawable(context, R.drawable.wallet)
            vectorDrawable?.let { drawable ->
                val imageRenderer = object : PieChartRenderer(this@with, animator, viewPortHandler) {
                    override fun drawExtras(c: Canvas) {
                        super.drawExtras(c)

                        val center = getCenterCircleBox()
                        val imageSize = 80f
                        val left = center.x - imageSize / 2
                        val top = center.y - imageSize / 2
                        val right = center.x + imageSize / 2
                        val bottom = center.y + imageSize / 2

                        drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        drawable.draw(c)
                    }
                }
                renderer = imageRenderer
                extraTopOffset = 0f
                extraBottomOffset = 0f

                invalidate()
            }
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

    private fun getRandomColor(): Int {
        val random = java.util.Random()
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256))
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