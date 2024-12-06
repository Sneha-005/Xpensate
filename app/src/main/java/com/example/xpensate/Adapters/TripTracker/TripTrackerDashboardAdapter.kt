package com.example.xpensate.Adapters.TripTracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.TripTracker.Data
import com.example.xpensate.databinding.TripTrackerDashboardItemsBinding

class TripTrackerDashboardAdapter(
    private var groupList: MutableList<Data>,
    private val onItemClick: (Data) -> Unit
) : RecyclerView.Adapter<TripTrackerDashboardAdapter.RecordViewHolder>() {

    class RecordViewHolder(private val binding: TripTrackerDashboardItemsBinding, private val onItemClick: (Data) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {
            binding.inviteCodeRecycler.text = data.invitecode ?: "No invite code"
            binding.tripNameRecycler.text = data.name ?: "Unnamed Trip"
            binding.root.setOnClickListener {
                onItemClick(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = TripTrackerDashboardItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val data = groupList.getOrNull(position)
        data?.let {
            holder.bind(it)
        }
    }

    override fun getItemCount(): Int = groupList.size

    fun updateRecords(newRecords: List<Data>) {
        groupList.clear()
        groupList.addAll(newRecords)
        notifyDataSetChanged()
    }
}