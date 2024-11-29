package com.example.xpensate.Adapters.DebtsAndLends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.DebtsAndLends.Debtmark
import com.example.xpensate.API.home.DebtsAndLends.DebtsList
import com.example.xpensate.AuthInstance
import com.example.xpensate.R
import com.example.xpensate.databinding.LendsRecordsItemBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LendsAdapter(private val recordList: MutableList<DebtsList>) :
    RecyclerView.Adapter<LendsAdapter.RecordViewHolder>() {

    inner class RecordViewHolder(private val binding: LendsRecordsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun markDebtAsPaid(debtId: String) {
            AuthInstance.api.markDebtPaid(debtId.toInt()).enqueue(object : Callback<Debtmark> {
                override fun onResponse(call: Call<Debtmark>, response: Response<Debtmark>) {
                    if (response.isSuccessful) {
                        binding.resolveButton.apply {
                            text = "Resolved"
                            setTextColor(context.resources.getColor(R.color.black))
                            setBackgroundColor(context.resources.getColor(R.color.green))
                            isEnabled = false
                        }

                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            recordList[position].is_paid = true
                        }

                        Toast.makeText(
                            binding.root.context,
                            "Marked as paid successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Failed to mark as paid. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Debtmark>, t: Throwable) {
                    Toast.makeText(
                        binding.root.context,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        fun bind(recordItem: DebtsList) {
            if (recordItem.lend) {
                binding.root.visibility = View.VISIBLE
                binding.name.text = recordItem.name
                binding.dateText.text = recordItem.date
                binding.amount.text = recordItem.amount
                if (recordItem.is_paid) {
                    binding.resolveButton.text = "Resolved"
                    binding.resolveButton.setTextColor(binding.root.context.resources.getColor(R.color.black))
                    binding.resolveButton.setBackgroundColor(
                        binding.root.context.resources.getColor(R.color.green)
                    )
                } else {
                    binding.resolveButton.apply {
                        text = "Resolved"
                        isEnabled = true
                        setOnClickListener {
                            isEnabled = false
                            text = "Processing..."

                            markDebtAsPaid(recordItem.id.toString())
                        }
                    }
                }
            } else {
                binding.root.visibility = View.GONE
            }
        }
    }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = LendsRecordsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(recordList[position])
    }

    override fun getItemCount(): Int = recordList.size

    fun updateRecords(newRecords: List<DebtsList>) {
        recordList.clear()
        recordList.addAll(newRecords.filter { it.lend })
        notifyDataSetChanged()
    }

}