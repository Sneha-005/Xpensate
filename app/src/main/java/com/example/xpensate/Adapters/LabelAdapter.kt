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

    override fun getItem(position: Int): LabelItem = labelList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.legend_item, parent, false)
            viewHolder = ViewHolder(
                colorCircle = view.findViewById(R.id.colorIndicator),
                labelText = view.findViewById(R.id.legendText)
            )
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val labelItem = getItem(position)
        viewHolder.labelText.text = labelItem.label

        val background = viewHolder.colorCircle.background
        if (background is GradientDrawable) {
            background.setColor(labelItem.color)
        }

        return view
    }

    fun updateLegend(newLabelList: List<LabelItem>?) {
        if (newLabelList != null) {
            labelList = newLabelList
            notifyDataSetChanged()
        }
    }
    private data class ViewHolder(
        val colorCircle: View,
        val labelText: TextView
    )
}