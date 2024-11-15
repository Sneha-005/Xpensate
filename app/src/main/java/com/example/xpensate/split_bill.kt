import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.example.xpensate.R

class split_bill : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_split_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barChart = view.findViewById<HorizontalBarChart>(R.id.barChart)

        val entries = mutableListOf(
            BarEntry(0f, 65f),
            BarEntry(1f, 35f)
        )

        val dataSet = BarDataSet(entries, "User Data")
        dataSet.colors = listOf(Color.GREEN, Color.YELLOW)
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.animateY(1500)

        barChart.setDrawGridBackground(false)
        barChart.description = Description().apply { text = "" }
        barChart.axisLeft.setDrawLabels(false)
        barChart.axisRight.setDrawLabels(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.legend.isEnabled = false

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> "Me"
                    1 -> "Arjun"
                    else -> ""
                }
            }
        }
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 14f
        xAxis.setDrawLabels(true)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val yAxis = barChart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}%"
            }
        }
        yAxis.textColor = Color.WHITE
        yAxis.textSize = 14f
        yAxis.setDrawLabels(true)
        yAxis.setDrawGridLines(false)

        barChart.invalidate()
    }
}