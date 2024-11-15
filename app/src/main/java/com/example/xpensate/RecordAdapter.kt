package com.example.xpensate

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.databinding.RecordsRecyclerBinding
import android.util.Log
import com.bumptech.glide.Glide

class RecordAdapter(private val recordList: MutableList<RecordsResponseItem>) :

    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(private val binding: RecordsRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recordItem: RecordsResponseItem) {
            binding.heading.text = recordItem.category
            binding.shortForm.text = recordItem.amount
            binding.dateText.text = recordItem.date
            val imageResource = recordItem.image as? Int

            if (imageResource != null) {
                binding.image.setImageResource(imageResource)
            } else {
                val imageUrl = recordItem.image as? String
                if (imageUrl != null) {
                    Glide.with(binding.image.context)
                        .load(imageUrl)
                        .placeholder(R.drawable.wallet)
                        .into(binding.image)
                } else {
                    binding.image.setImageResource(R.drawable.wallet)
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