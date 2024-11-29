package com.example.xpensate.Adapters.SlitBillFeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.SplitBillFeature.GroupMembers.Data
import com.example.xpensate.R
import com.example.xpensate.databinding.AddMemberItemBinding

class AddmemberAdapter(
    private var groupMembers: List<Data>,
    private val onMemberRemoved: (String) -> Unit,
    private val onGroupSelected: (String) -> Unit? = {}
) : RecyclerView.Adapter<AddmemberAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(private val binding: AddMemberItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupMember: Data) {
            binding.name.text = groupMember.member.name ?: "No Name"
            binding.email.text = groupMember.member.email
            binding.option.setOnClickListener {
                groupMember.member.email?.let { email ->
                    onMemberRemoved(email)
                }
            }
            binding.root.setOnClickListener {
                groupMember.member.email?.let { email ->
                    onGroupSelected(email)
                }
            }
            binding.image.setImageResource((groupMember.member.profile_image as? Int ?: R.drawable.avatar3))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = AddMemberItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(groupMembers[position])
    }

    override fun getItemCount(): Int = groupMembers.size

    fun updateData(newGroupMembers: List<Data>) {
        groupMembers = newGroupMembers
        notifyDataSetChanged()
    }
}
