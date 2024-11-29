package com.example.xpensate.Adapters.SplitBillFeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.SplitBillFeature.Groups.MembersGroup
import com.example.xpensate.API.home.SplitBillFeature.Groups.OwnerGroup
import com.example.xpensate.Adapters.OverlappingImagesAdapter
import com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature.split_bill
import com.example.xpensate.R
import com.example.xpensate.databinding.BillSplitItemsBinding

class SplitBillFeatureAdapter(
    private var groups: List<Any>,
    private val listener: split_bill
) : RecyclerView.Adapter<SplitBillFeatureAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(private val binding: BillSplitItemsBinding, private val context: android.content.Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Any) {
            binding.heading.text = when (group) {
                is OwnerGroup -> group.name
                is MembersGroup -> group.name
                else -> "Unknown Group"
            }
            binding.root.setOnClickListener {
                listener.onItemClick(group)
            }
            binding.splitButton.setOnClickListener{
                listener.onSplitButtonClick(group)
            }
            binding.addMemberButton.setOnClickListener {
                listener.onAddMemberButtonClick(group)
            }


            val imageList = when(group){
                is MembersGroup -> group.members.map { it.profile_image as? Int ?: R.drawable.avatar3 }
                else -> listOf(R.drawable.avatar3)
            }
            binding.overlappingImagesRecycler.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )

            val adapter = OverlappingImagesAdapter(imageList)
            binding.overlappingImagesRecycler.adapter = adapter
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = BillSplitItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.bind(group)
    }

    override fun getItemCount(): Int = groups.size

    fun updateData(newGroups: List<Any>) {
        groups = newGroups
        notifyDataSetChanged()
    }


}