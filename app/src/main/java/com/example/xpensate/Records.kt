package com.example.xpensate

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.AuthInstance
import com.example.xpensate.RecordAdapter
import com.example.xpensate.RecordsResponse
import com.example.xpensate.RecordsResponseItem
import com.example.xpensate.databinding.FragmentRecordsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class Records : Fragment() {

    private var _binding: FragmentRecordsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecordsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = RecordAdapter(mutableListOf())
        binding.recordContainer.layoutManager = LinearLayoutManager(context)
        binding.recordContainer.adapter = adapter

        fetchRecordsData(adapter)

    }

   private fun fetchRecordsData(adapter: RecordAdapter) {
       CoroutineScope(Dispatchers.IO).launch {
           try {
               val response = AuthInstance.api.expenselist().execute()
               if (response.isSuccessful) {
                   val recordsResponse = response.body()

                   if (recordsResponse != null && recordsResponse.expenses.isNotEmpty()) {
                       val recordsList = recordsResponse.expenses

                       withContext(Dispatchers.Main) {
                           adapter.updateRecords(recordsList)
                           val totalExpense = recordsResponse.total_expense
                           binding.totalExpenseTextView.text = "₹$totalExpense"
                       }

                   } else {
                       Log.d("API Response", "No records found")
                   }
               } else {
                   Log.e("API Error", "Response code: ${response.code()}")
               }
           } catch (e: Exception) {
               Log.e("Network Error", "Exception: ${e.message}")
           }
       }
   }

    override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

