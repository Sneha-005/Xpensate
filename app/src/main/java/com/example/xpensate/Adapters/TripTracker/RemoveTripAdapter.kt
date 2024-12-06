package com.example.xpensate.Adapters.TripTracker

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.TripTracker.CreateGroupResponse.Member
import com.example.xpensate.API.TripTracker.RemoveDataResponse
import com.example.xpensate.AuthInstance
import com.example.xpensate.ProgressDialogHelper
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
            try {
                val memberName = member.user?.name ?: "Unknown Member"
                val memberEmail = member.user?.email ?: "No email"

                binding.Name.text = memberName
                binding.email.text = memberEmail
                binding.remove.setOnClickListener {
                    if (memberEmail != "No email") {
                        removeGroupMember(groupId, memberEmail)
                    } else {
                        Toast.makeText(context, "Invalid member", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("RemoveTripAdapter", "Error binding data: ${e.message}")
                Toast.makeText(context, "Error binding data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = RemoveTripItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        try {
            memberList.getOrNull(position)?.let { member ->
                holder.bind(member)
            }
        } catch (e: Exception) {
            Log.e("RemoveTripAdapter", "Error binding item at position $position: ${e.message}")
            Toast.makeText(context, "Error binding item", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = memberList.size

    private fun removeGroupMember(groupId: String, memberEmail: String) {
        ProgressDialogHelper.showProgressDialog(context)
        try {
            AuthInstance.api.removeTripMember(groupId, memberEmail).enqueue(object :
                Callback<RemoveDataResponse> {
                override fun onResponse(
                    call: Call<RemoveDataResponse>,
                    response: Response<RemoveDataResponse>
                ) {
                    try {
                        Log.d("RemoveTripAdapter", "group id: $groupId, email: $memberEmail")
                        ProgressDialogHelper.hideProgressDialog()
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Member removed successfully", Toast.LENGTH_SHORT).show()
                            onMemberRemoved()
                        } else {
                            Log.e("RemoveTripAdapter", "Error removing member: ${response.code()} - ${response.message()}")
                            Toast.makeText(context, "Failed to remove member: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("RemoveTripAdapter", "Error handling response: ${e.message}")
                        Toast.makeText(context, "Error handling response", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RemoveDataResponse>, t: Throwable) {
                    try {
                        Log.e("RemoveTripAdapter", "Error: ${t.message}", t)
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Log.e("RemoveTripAdapter", "Error in onFailure: ${e.message}")
                    } finally {
                        ProgressDialogHelper.hideProgressDialog()
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("RemoveTripAdapter", "Error making network call: ${e.message}")
            Toast.makeText(context, "Error removing member: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateData(newGroupMembers: List<Member>) {
        try {
            memberList = newGroupMembers
            notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("RemoveTripAdapter", "Error updating data: ${e.message}")
            Toast.makeText(context, "Error updating data", Toast.LENGTH_SHORT).show()
        }
    }
}

