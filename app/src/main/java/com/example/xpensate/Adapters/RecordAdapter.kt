package com.example.xpensate.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.databinding.RecordsRecyclerBinding
import com.bumptech.glide.Glide
import com.example.xpensate.Modals.RecordsResponseItem
import com.example.xpensate.R

class RecordAdapter(private val recordList: MutableList<RecordsResponseItem>) :

    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {
    private val imageMap = mapOf(
        "Food and Drinks" to R.drawable.food_icon,
        "Housing" to R.drawable.housing_icon,
        "Shopping" to R.drawable.shopping_icon,
        "Investment" to R.drawable.investment_icon,
        "Life & Entertainment" to R.drawable.life_icon,
        "Technical Appliance" to R.drawable.tech_icon,
        "Transportation" to R.drawable.transportation_icon
    )
    inner class RecordViewHolder(private val binding: RecordsRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recordItem: RecordsResponseItem) {
            binding.heading.text = recordItem.category
            binding.shortForm.text = recordItem.amount
            binding.dateText.text = recordItem.date

            if(recordItem.is_credit == true) {
                binding.shortForm.setTextColor(binding.root.context.resources.getColor(R.color.green))
            }else{
                binding.shortForm.setTextColor(binding.root.context.resources.getColor(R.color.red))
            }

            val imageResource = imageMap[recordItem.category] ?: R.drawable.other_icon
            binding.image.setImageResource(imageResource)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = RecordsRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(recordList[position])
    }

    override fun getItemCount(): Int = recordList.size

    fun updateRecords(newRecords: List<RecordsResponseItem>) {
        recordList.clear()
        recordList.addAll(newRecords)
        notifyDataSetChanged()
    }
}