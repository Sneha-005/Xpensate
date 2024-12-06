package com.example.xpensate.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.Modals.CurrencyClass
import com.example.xpensate.Modals.getSymbol
import com.example.xpensate.OnCurrencySelectedListener
import com.example.xpensate.databinding.FragmentCurrencyConverterRecyclerBinding

class CurrencyAdapter(
    private val currencyList: List<List<String>>,
    private val currencyClass: CurrencyClass,
    private val listener: OnCurrencySelectedListener
) : RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    private val originalList = ArrayList(currencyList)
    private var filteredList = ArrayList(currencyList)

    inner class CurrencyViewHolder(private val binding: FragmentCurrencyConverterRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currencyItem: List<String>) {
            val shortForm = currencyItem.getOrNull(0) ?: "N/A"
            val heading = currencyItem.getOrNull(1) ?: "N/A"
            val symbol = getSymbol(shortForm, currencyClass) ?: "N/A"
            binding.heading.text = heading
            binding.shortForm.text = shortForm
            binding.symbol.text = symbol
            binding.root.setOnClickListener {
                Toast.makeText(itemView.context, "Currency Selected: $shortForm", Toast.LENGTH_SHORT).show()
                listener.onCurrencySelected(shortForm, heading, symbol)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val binding = FragmentCurrencyConverterRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrencyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(filteredList[position])
    }

    override fun getItemCount(): Int = filteredList.size

    fun filter(query: String) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(currencyList)
        } else {
            originalList.filter { currencyItem ->
                currencyItem.getOrNull(0)?.contains(query, ignoreCase = true) == true ||
                        currencyItem.getOrNull(1)?.contains(query, ignoreCase = true) == true
            }.let { filteredResults ->
                filteredList.addAll(filteredResults)
            }
        }
        notifyDataSetChanged()
    }
}