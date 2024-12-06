package com.example.xpensate.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.BillParticipant
import com.example.xpensate.API.home.Data
import com.example.xpensate.R
import com.example.xpensate.TokenDataStore
import com.example.xpensate.databinding.RecentBillSplitItemBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SplitBillAdapter(private var billList: MutableList<Data>) :
    RecyclerView.Adapter<SplitBillAdapter.SplitBillViewHolder>() {

    inner class SplitBillViewHolder(private val binding: RecentBillSplitItemBinding, private val context: android.content.Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bill: Data) {
            val dateTimeString = bill.billdate ?: "N/A"
            val formattedDate = formatDate(dateTimeString)
            binding.amountSplit.text = bill.amount ?: "0"
            binding.SplitDate.text = formattedDate
            binding.category.text = bill.billname ?: "Unknown"

            (context as? androidx.fragment.app.FragmentActivity)?.lifecycleScope?.launch {
                try {
                    val email = TokenDataStore.getEmail(context).first()
                    val userPaidAmount = bill.bill_participants
                        ?.firstOrNull { it.participant?.email == email }
                        ?.amount ?: "N/A"
                    if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                        binding.userPaid.text = userPaidAmount
                    }
                } catch (e: Exception) {
                    Log.e("SplitBillAdapter", "Error fetching user paid amount: ${e.message}")
                }
            }

            val imageList = bill.bill_participants?.map {
                try {
                    it.participant?.profile_image as? Int ?: R.drawable.avatar3
                } catch (e: Exception) {
                    R.drawable.avatar3
                }
            } ?: listOf(R.drawable.avatar3)
            binding.overlappingImagesRecycler.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )

            val adapter = OverlappingImagesAdapter(imageList)
            binding.overlappingImagesRecycler.adapter = adapter
        }
        fun formatDate(dateTimeString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val date: Date? = inputFormat.parse(dateTimeString)
                if (date != null) outputFormat.format(date) else "N/A"
            } catch (e: Exception) {
                Log.e("SplitBillAdapter", "Date parsing error: ${e.message}")
                "N/A"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SplitBillViewHolder {
        val binding = RecentBillSplitItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SplitBillViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: SplitBillViewHolder, position: Int) {
        holder.bind(billList[position])
    }

    override fun getItemCount(): Int = billList.size

    fun updateSplits(newSplit: List<Data>) {
        billList.clear()
        billList.addAll(newSplit)
        notifyDataSetChanged()
    }
}