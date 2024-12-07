package com.example.xpensate.Fragments.Dashboard

import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.example.xpensate.API.BudgetBuilder.BudgetExpensesResponse
import com.example.xpensate.API.home.CategoryChartResponse
import com.example.xpensate.Adapters.LabelAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.Adapters.RecordAdapter
import com.example.xpensate.Adapters.SplitBillAdapter
import com.example.xpensate.Modals.LabelItem
import com.example.xpensate.ProgressDialogHelper
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.Dispatchers

class BlankFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentBlankBinding? = null
    private val binding get() = _binding!!
    private var adapter: LabelAdapter? = null
    private val calendar = Calendar.getInstance()
    private val startDate: String
    private val endDate: String
    private val lastApiCallTime = MutableSharedFlow<Unit>(replay = 1)

    init {
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

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
            navController.navigate(R.id.action_blankFragment_to_budgetBuilderShow)
        }
        binding.moreRecords.setOnClickListener {
            navController.navigate(R.id.action_blankFragment_to_records)
        }

        val pieChart = _binding?.pieChart
        val legendListView = _binding?.legendListView

        val splitAdapter = SplitBillAdapter(mutableListOf())
        val splitRecyclerView = binding.billSplitRecycler
        splitRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        splitRecyclerView.adapter = splitAdapter
        fetchSplitBills(splitAdapter)

        val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        binding.monthText.text = currentMonth

        val recordAdapter = RecordAdapter(mutableListOf(),findNavController())
        val recordRecyclerView = binding.recordContainer
        recordRecyclerView.layoutManager = LinearLayoutManager(context)
        recordRecyclerView.adapter = recordAdapter

        fetchRecordsData(recordAdapter)
        fetchUserBudget()
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
        lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val response = AuthInstance.api.getSplitGroups().execute()
                        if (response.isSuccessful) {
                            val splitBillsResponse = response.body()
                            if (splitBillsResponse != null && splitBillsResponse.data.isNotEmpty()) {
                                val splitList = splitBillsResponse.data
                                withContext(Dispatchers.Main) {
                                    adapter.updateSplits(splitList)
                                    binding.billSplitRecycler.visibility = View.VISIBLE
                                    binding.noSplit.visibility = View.GONE
                                }
                            } else {
                                binding.billSplitRecycler.visibility = View.GONE
                                binding.noSplit.visibility = View.VISIBLE
                                Log.d("API Response", "No records found or data is null")
                            }
                        } else {
                            if(response.code() == 500){
                                Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                            }
                            val errorBody = response.message().toString()
                            Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                            Log.e("API Error", "Response code: ${response.code()}")
                            Log.e("API Error", "Error: ${response.errorBody()?.string()}")
                        }
                    } catch (e: Exception) {
                        Log.e("Network Error", "Exception: ${e.message}")
                    }
        }
    }
    private fun fetchUserBudget() {
        AuthInstance.api.getBudgetExpenses().enqueue(object : Callback<BudgetExpensesResponse> {

            override fun onResponse(
                call: Call<BudgetExpensesResponse>,
                response: Response<BudgetExpensesResponse>
            ) {
                if (response.isSuccessful) {
                    val budgetResponse = response.body()
                    if (budgetResponse != null) {
                        val totalDebit = budgetResponse.luxury_debit_total.plus(budgetResponse.needs_debit_total)
                        val totalIncome = budgetResponse.monthly
                        val text = "Spent ₹$totalDebit Of ₹$totalIncome monthly"
                        val spannableString = SpannableString(text)
                        val start = text.indexOf("₹")
                        val end = text.indexOf(" Of")
                        spannableString.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.green)),
                            start,
                            end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        if (_binding != null) {
                            binding.progressText.text = spannableString
                            setUpCircularSeekBar(totalIncome, totalDebit)
                        }
                    } else {
                        Log.e("API Error", "Budget response is null")
                        if (_binding != null) {
                            Toast.makeText(
                                requireContext(),
                                "Failed to fetch budget data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Log.e("API Error", "Response code: ${response.code()} | Error: ${response.errorBody()?.string()}")
                    if (_binding != null) {
                        Toast.makeText(
                            requireContext(),
                            "Failed to fetch budget data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<BudgetExpensesResponse>, t: Throwable) {
                Log.e("Network Error", "Exception: ${t.message}")
                if (_binding != null) {
                    Toast.makeText(requireContext(), "Failed to fetch data. Check your network connection.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun fetchRecordsData(adapter: RecordAdapter) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = AuthInstance.api.expenselist(startDate, endDate).execute()
                if (response.isSuccessful) {
                    val recordsResponse = response.body()
                    if (recordsResponse != null && recordsResponse.expenses.isNotEmpty()) {
                        val recordsList = recordsResponse.expenses.take(4)
                        withContext(Dispatchers.Main) {
                            adapter.updateRecords(recordsList)
                            binding.recordContainer.visibility = View.VISIBLE
                            binding.noRecord.visibility = View.GONE
                        }
                    } else{
                        binding.recordContainer.visibility = View.GONE
                        binding.noRecord.visibility = View.VISIBLE
                        Toast.makeText(requireContext(),"Server Error",Toast.LENGTH_SHORT).show()

                    }
                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Exception: ${e.message}")
            }
        }
    }

    private fun setLineChartData() {
        val lineChart = binding.lineChart
        val xAxis = lineChart.xAxis
        xAxis.apply {
            axisMinimum = 0f
            axisMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH).toFloat()
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            textColor = Color.BLACK
            textSize = 12f
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        lineChart.apply {
            axisLeft.isEnabled = false
            axisRight.isEnabled = false
            setNoDataText("No data available")
            setNoDataTextColor(Color.WHITE)
        }
        ProgressDialogHelper.showProgressDialog(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    AuthInstance.api.getLineGraphData(startDate, endDate).execute()
                }
                if (!isAdded || _binding == null) return@launch
                if (response.isSuccessful) {
                    ProgressDialogHelper.hideProgressDialog()
                    val lineGraphData = response.body()
                    val amount = (lineGraphData?.total_expenses?: 0.0)
                    val total = amount
                    Log.d("total","$total")

                    binding.balanceText.text = "₹%.2f".format(total)

                    if (!lineGraphData?.expenses_by_day.isNullOrEmpty()) {
                        val lineEntries = ArrayList<Entry>()
                        val fullMonthData = mutableMapOf<Int, Float>().apply {
                            for (day in 1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                                this[day] = 0f
                            }
                        }
                        lineGraphData?.expenses_by_day?.forEach { expensesByDay ->
                            val day = expensesByDay.date.split("-").last().toInt()
                            fullMonthData[day] = expensesByDay.total.toFloat()
                        }

                        fullMonthData.toSortedMap().forEach { (day, totalExpense) ->
                            lineEntries.add(Entry(day.toFloat(), totalExpense))
                        }

                        xAxis.valueFormatter = IndexAxisValueFormatter((1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).map { it.toString() })

                        val lineDataSet = LineDataSet(lineEntries, "Expenses by Day").apply {
                            color = Color.WHITE
                            setDrawFilled(true)
                            fillAlpha = 30
                            lineWidth = 4f
                            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.fade_white)
                            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                            setDrawCircles(false)
                        }
                        val data = LineData(lineDataSet).apply { setDrawValues(false) }
                        lineChart.data = data
                        lineChart.legend.isEnabled = false
                        lineChart.description.isEnabled = false
                        lineChart.invalidate()
                    } else {
                        Toast.makeText(requireContext(), "No data available to display", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if(response.code() == 500){
                        Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                    }
                    val errorBody = response.message().toString()
                    Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Network Error", "Exception: ${e.message}")
                Toast.makeText(requireContext(), "Network error occurred", Toast.LENGTH_SHORT).show()
            }finally {
                ProgressDialogHelper.hideProgressDialog()

            }
        }
    }

    class IndexAxisValueFormatter(private val days: List<String>) : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index in days.indices) String.format("%02d", days[index].toInt()) else ""
        }
    }

    private fun setUpCircularSeekBar(totalIncome: Int, totalSpent: Double) {
        val circularSeekBar = binding.circularSeekBar

        val progressPercentage = if (totalIncome > 0) {
            (totalSpent / totalIncome) * 100f
        } else {
            0f
        }

        circularSeekBar.max = 100f
        circularSeekBar.progress = progressPercentage.toFloat()
        circularSeekBar.min = 0f
        circularSeekBar.progressGradientColorsArray = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.greenShade),
            ContextCompat.getColor(requireContext(), R.color.green)
        )
        circularSeekBar.interactive = false
        circularSeekBar.innerThumbColor = ContextCompat.getColor(requireContext(), R.color.black)
        circularSeekBar.outerThumbColor = ContextCompat.getColor(requireContext(), R.color.greenShade)
        circularSeekBar.outerThumbRadius = 36f
        circularSeekBar.innerThumbRadius = 16f
        circularSeekBar.setOnTouchListener { _, _ -> false }
    }

    private fun setupPieChart(pieChart: PieChart) {
        ProgressDialogHelper.showProgressDialog(requireContext())
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
                                val proportion = (category.total.toFloat() / totalSpent)*100
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
                                setCenterText("₹%.2f".format(totalSpent))
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
                            Toast.makeText(requireContext(), "No data available", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if(response.code() == 500){
                            Toast.makeText(requireContext(),"Something went wrong",Toast.LENGTH_SHORT).show()
                        }
                        val errorBody = response.message().toString()
                        Toast.makeText(requireContext(),"$errorBody",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CategoryChartResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    private fun populateCustomLegend(categoryChartData: CategoryChartResponse, colors: List<Int>) {
        val labelItems = categoryChartData.expenses_by_category
            .filter { it.total > 0 }
            .mapIndexed { index, category ->
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