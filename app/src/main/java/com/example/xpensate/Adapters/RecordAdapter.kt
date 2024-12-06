package com.example.xpensate.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.databinding.RecordsRecyclerBinding
import com.bumptech.glide.Glide
import com.example.xpensate.Fragments.Dashboard.BlankFragmentDirections
import com.example.xpensate.Fragments.Dashboard.RecordsDirections
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.split_bill
import com.example.xpensate.Modals.RecordsResponseItem
import com.example.xpensate.R

class RecordAdapter(private val recordList: MutableList<RecordsResponseItem>, private val navController: NavController) :

    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {
    private val imageMap = mapOf(
        "Food and Drinks" to R.drawable.food_icon,
        "Housing" to R.drawable.housing_icon,
        "Shopping" to R.drawable.shopping_icon,
        "Investment" to R.drawable.investment_icon,
        "Life and Entertainment" to R.drawable.life_icon,
        "Technical Appliance" to R.drawable.tech_icon,
        "Transportation" to R.drawable.transportation_icon
    )
    inner class RecordViewHolder(private val binding: RecordsRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recordItem: RecordsResponseItem) {
            val recordCategory = recordItem.category ?: "Other"
            val recordAmount = recordItem.amount ?: "N/A"
            val recordDate = recordItem.date ?: "Unknown Date"
            val isCredit = recordItem.is_credit ?: false

            binding.heading.text = recordCategory
            binding.shortForm.text = recordAmount
            binding.dateText.text = recordDate

            val textColor = if (isCredit) {
                ContextCompat.getColor(binding.root.context, R.color.green)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.red)
            }
            binding.shortForm.setTextColor(textColor)

            val imageResource = imageMap[recordCategory] ?: R.drawable.other_icon
            binding.image.setImageResource(imageResource)

            binding.root.setOnClickListener {
                val action = RecordsDirections.actionRecordsToDeleteRecord(recordItem.id.toString())
                try {
                    navController.navigate(action)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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