package com.example.xpensate.Adapters.SlitBillFeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.SplitBillFeature.SplitGroupShowDetails.BillParticipant
import com.example.xpensate.databinding.SplitContactDetailsCheckBinding
import com.example.xpensate.databinding.SplitGroupShowItemBinding

class MemberDetailAdapter(
    private val bills: List<BillParticipant>
): RecyclerView.Adapter<MemberDetailAdapter.BillViewHolder>(){

    inner class BillViewHolder(private val binding: SplitContactDetailsCheckBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BillParticipant){
            if(item.paid){
                binding.checkbox.setChecked(true)
            }
            binding.name.text = item.participant.email
            binding.amountBill.text = item.amount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val binding = SplitContactDetailsCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position])
    }

    override fun getItemCount(): Int = bills.size


}