package com.example.xpensate.Adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.xpensate.Modals.LabelItem
import com.example.xpensate.R

class LabelAdapter(
    private var context: Context,
    private var labelList: List<LabelItem>
) : BaseAdapter() {

    override fun getCount(): Int = labelList.size

    override fun getItem(position: Int): Any = labelList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.legend_item, parent, false)

        val labelItem = getItem(position) as LabelItem

        val colorCircle = view.findViewById<View>(R.id.colorIndicator)
        val labelText = view.findViewById<TextView>(R.id.legendText)

        labelText.text = labelItem.label

        (colorCircle.background as GradientDrawable).setColor(labelItem.color)

        return view
    }
    fun updateLegend(newLabelList: List<LabelItem>) {
        labelList = newLabelList
        notifyDataSetChanged()
    }

}
