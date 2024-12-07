package com.example.xpensate.Adapters.SlitBillFeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.SplitBillFeature.SplitGroupShowDetails.Bill
import com.example.xpensate.databinding.SplitGroupShowItemBinding

class SplitAmountShowAdapter (
    private var bills: MutableList<Bill>,
    private val ownerName: String,
    private val markBillAsPaid: (String, String, Boolean) -> Unit

): RecyclerView.Adapter<SplitAmountShowAdapter.BillViewHolder>(){

    inner class BillViewHolder(private val binding: SplitGroupShowItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Bill){
            binding.userPaid.text = item.amount
            binding.ownerName.text = ownerName
            if (binding.contactContainer.layoutManager == null) {
                binding.contactContainer.layoutManager = LinearLayoutManager(
                    binding.root.context, LinearLayoutManager.VERTICAL, false
                )
            }
            if (item.bill_participants.isNotEmpty()) {
                val adapter = MemberDetailAdapter(item.bill_participants,item.id.toString(), markBillAsPaid)
                binding.contactContainer.adapter = adapter
            } else {
                binding.contactContainer.adapter = null
            }
            item.bill_participants.map { it.paid == true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = SplitGroupShowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position])
    }

    override fun getItemCount(): Int = bills.size

    fun updateSplits(newSplit: List<Bill>) {
        bills.clear()
        bills.addAll(newSplit)
        notifyDataSetChanged()
    }
}