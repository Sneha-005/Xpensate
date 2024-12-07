package com.example.xpensate.Adapters.SlitBillFeature

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.SplitBillFeature.BillParticipantX
import com.example.xpensate.databinding.SplitAmountPageItemBinding

class SplitAmountpageAdapter(
    val items: MutableList<BillParticipantX>,
    private val groupId: String,
    private val markBillAsPaid: (String, String, Boolean) -> Unit
) : RecyclerView.Adapter<SplitAmountpageAdapter.TableViewHolder>() {

    private val checkboxState = mutableMapOf<String, Boolean>()
    var totalAmount: Double = 0.0
    inner class TableViewHolder(private val binding: SplitAmountPageItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BillParticipantX) {
            binding.name.text = item.participant

            binding.share.setText(item.amount.toString())

            binding.share.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val inputText = s.toString().replace("%", "").trim()
                    val sharePercentage = inputText.toDoubleOrNull()

                }

            })

            binding.checkbox.isChecked = checkboxState[item.participant] ?: false
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                checkboxState[item.participant] = isChecked
                markBillAsPaid(groupId, item.participant, isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val binding = SplitAmountPageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}