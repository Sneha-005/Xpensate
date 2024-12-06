package com.example.xpensate.Adapters.TripTracker

import android.graphics.Color
import android.util.Log
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
        fun bind(expense: Expense) {
            Log.d("expense","$expense")
            binding.Name.text = expense.paidby_email?: "No name"
            binding.amount.text = expense.amount.toString()?: "0.00"
            val amountValue = expense.amount.toDoubleOrNull() ?: 0.0

            if (expense.is_paid) {
                binding.paidButton.setBackgroundColor(Color.GREEN)
                binding.paidButton.text = "Paid ${formatShare(expense.share)}"
            } else {
                binding.paidButton.setBackgroundColor(Color.RED)
                binding.paidButton.text = "Pay ${formatShare(expense.share)}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = SelectedTripDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        groupList.getOrNull(position)?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = groupList.size

    fun updateRecords(newRecords: List<Expense>) {
        groupList.clear()
        groupList.addAll(newRecords)
        notifyDataSetChanged()
    }
}
