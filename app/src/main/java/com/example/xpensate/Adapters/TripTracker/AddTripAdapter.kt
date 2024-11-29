package com.example.xpensate.Adapters.TripTracker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.xpensate.API.TripTracker.CreateGroupResponse.Member
import com.example.xpensate.R
import com.example.xpensate.databinding.AddTripItemsBinding

class AddTripAdapter(
    private var memberList: List<Member>,
    private val context: Context
) : RecyclerView.Adapter<AddTripAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(private val binding: AddTripItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: Member) {
            binding.name.text = member.user.name
            binding.email.text = member.user.email

            Glide.with(context)
                .load(member.user.profile_image)
                .placeholder(R.drawable.avatar3)
                .error(R.drawable.avatar3)
                .into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = AddTripItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(memberList[position])
    }

    override fun getItemCount(): Int = memberList.size

    fun updateData(newGroupMembers: List<Member>) {
        memberList = newGroupMembers
        notifyDataSetChanged()
    }
}
