package com.example.xpensate.Adapters.TripTracker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.TripTracker.CreateGroupResponse.Member
import com.example.xpensate.API.TripTracker.RemoveDataResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.databinding.RemoveTripItemsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RemoveTripAdapter(
    private var memberList: List<Member>,
    private val groupId: String,
    private val context: Context,
    private val onMemberRemoved: () -> Unit
) : RecyclerView.Adapter<RemoveTripAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(private val binding: RemoveTripItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            binding.Name.text = member.user.name
            binding.email.text = member.user.email
            binding.remove.setOnClickListener {
                removeGroupMember(groupId, member.user.email)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = RemoveTripItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(memberList[position])
    }

    override fun getItemCount(): Int = memberList.size

    private fun removeGroupMember(groupId: String, memberEmail: String) {
        AuthInstance.api.removeTripMember(groupId, memberEmail).enqueue(object :
            Callback<RemoveDataResponse> {
            override fun onResponse(call: Call<RemoveDataResponse>, response: Response<RemoveDataResponse>) {
                Log.d("RemoveTripAdapter", "group id: $groupId, email: $memberEmail")
                if (response.isSuccessful) {
                    Toast.makeText(context, "Member removed successfully", Toast.LENGTH_SHORT).show()
                    onMemberRemoved()
                } else {
                    Toast.makeText(context, "Failed to remove member: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RemoveDataResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun updateData(newGroupMembers: List<Member>) {
        memberList = newGroupMembers
        notifyDataSetChanged()
    }
}
