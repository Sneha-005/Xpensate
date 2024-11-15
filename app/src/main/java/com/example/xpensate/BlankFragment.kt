package com.example.xpensate


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
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.home.lineGraph
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.databinding.FragmentBlankBinding
import com.example.xpensate.network.ApiService
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BlankFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!

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

        val adapter = RecordAdapter(mutableListOf())
        binding.recordContainer.layoutManager = LinearLayoutManager(context)
        binding.recordContainer.adapter = adapter

        fetchRecordsData(adapter)

        setLineChartData()
        setupSemiCirclePieChart(binding.semipieChart)
        setupPieChart(binding.pieChart)

        binding.drawerIconButton.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            val navigationView = activity?.findViewById<View>(R.id.navigation_view)

            if (drawerLayout != null && navigationView != null) {
                drawerLayout.openDrawer(navigationView)
            }
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

  private fun fetchRecordsData(adapter: RecordAdapter) {
      CoroutineScope(Dispatchers.IO).launch {
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

    fun setLineChartData() {
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
                    if (response.isSuccessful) {
                        val lineGraphData = response.body()

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

                            val drawable =
                                ContextCompat.getDrawable(requireContext(), R.drawable.fade_white)
                            lineDataSet.fillDrawable = drawable

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

    fun setupSemiCirclePieChart(semiPieChart: PieChart) {
        val semipieEntries = ArrayList<PieEntry>()
        semipieEntries.add(PieEntry(80f, "Category 1"))
        semipieEntries.add(PieEntry(20f, "Category 2"))

        val semipieDataSet = PieDataSet(semipieEntries, "")
        semipieDataSet.colors = listOf(
            Color.GREEN,
            Color.GRAY,
        )

        val semipieData = PieData(semipieDataSet)
        semipieData.setDrawValues(false)

        with(binding.semipieChart) {
            data = semipieData
            isDrawHoleEnabled = true
            rotationAngle = 180f
            maxAngle = 180f
            description.isEnabled = false
            legend.isEnabled = false
            isRotationEnabled = false
            transparentCircleRadius = 75f
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 90f
            setCenterText("Spent of ₹0")
            setCenterTextColor(Color.WHITE)
            setCenterTextSize(16f)
            setDrawEntryLabels(false)
            setDrawMarkers(false)


            setExtraOffsets(0f, -10f, 0f, -10f)

            invalidate()
        }
    }

   fun setupPieChart(pieChart: PieChart) {
       viewLifecycleOwner.lifecycleScope.launch {
           AuthInstance.api.expenseChart().enqueue(object : Callback<CategoryChartResponse> {
               override fun onResponse(
                   call: Call<CategoryChartResponse>,
                   response: Response<CategoryChartResponse>
               ) {
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

                           val randomColors = categoryChartData.expenses_by_category.map {
                               getRandomColor()
                           }
                           pieDataSet.colors = randomColors

                           val pieData = PieData(pieDataSet)
                           pieData.setDrawValues(false)

                           with(pieChart) {
                               data = pieData
                               setHoleColor(Color.TRANSPARENT)
                               holeRadius = 70f
                               setCenterText("₹$totalSpent")
                               setCenterTextColor(Color.WHITE)
                               setCenterTextSize(16f)

                               description.isEnabled = false
                               legend.isEnabled = true
                               legend.setDrawInside(false)
                               legend.form = Legend.LegendForm.CIRCLE
                               legend.textColor = Color.WHITE
                               legend.isWordWrapEnabled = true

                               setDrawEntryLabels(false)
                               invalidate()
                           }
                       } else {
                           Log.d("PieChart", "No data available for the Pie chart")
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