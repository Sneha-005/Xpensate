package com.example.xpensate.Adapters.TripTracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.TripTracker.CreateGroupResponse.Expense
import com.example.xpensate.databinding.SelectedTripDetailsItemBinding

class SelectedTripDetailsAdapter(private var groupList: MutableList<Expense>) :
    RecyclerView.Adapter<SelectedTripDetailsAdapter.RecordViewHolder>() {

    private fun formatShare(share: Double): String {
        return String.format("%.2f", share)
    }
    inner class RecordViewHolder(private val binding: SelectedTripDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(groupList: List<Expense>) {
            binding.Name.text = groupList.joinToString { it.paidby_email }
            binding.amount.text = groupList.joinToString { it.amount }
            if(groupList.map { it.is_paid }.contains(true)){
                binding.paidButton.setBackgroundColor(0x00FF00)
                binding.paidButton.text = "Paid "+ groupList.joinToString { formatShare(it.share) }
            }
            else{
                binding.paidButton.text = "Pay" + groupList.joinToString { formatShare(it.share) }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = SelectedTripDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(listOf(groupList[position]))
    }

    override fun getItemCount(): Int = groupList.size

    fun updateRecords(newRecords: List<Expense>) {
        groupList.clear()
        groupList.addAll(newRecords)
        notifyDataSetChanged()
    }
}