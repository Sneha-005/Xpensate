package com.example.xpensate.Fragments.Dashboard.TripTracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xpensate.API.TripTracker.Data
import com.example.xpensate.Adapters.TripTracker.TripTrackerDashboardAdapter
import com.example.xpensate.AuthInstance
import com.example.xpensate.databinding.FragmentTripTrackerDashBoardBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.awaitResponse

class TripTrackerDashBoard : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentTripTrackerDashBoardBinding? = null
    private val binding get() = _binding!!
    private val tripTrackAdapter = TripTrackerDashboardAdapter(mutableListOf()) { data: Data ->
        navigateToDetailPage(data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTripTrackerDashBoardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        binding.tripsContainer.apply {
            adapter = tripTrackAdapter
            layoutManager = LinearLayoutManager(context)
        }
        userGroupDetail()
        binding.createButton.setOnClickListener {
            createTrip(binding.tripName.text.toString().trim())
        }
        binding.joinButton.setOnClickListener {
            joinGroup(binding.inviteCode.text.toString().trim())
        }
    }

    private fun createTrip(name: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthInstance.api.createTrip(name).awaitResponse()
                if (response.isSuccessful) {
                    Toast.makeText(context, "Group is created successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(context, "HTTP Exception: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Throwable) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToDetailPage(data: Data) {
        val action = TripTrackerDashBoardDirections.actionTripTrackerDashBoardToSelectedTripDetails(data.id.toString(), data.name)
        findNavController().navigate(action)
    }

    private fun joinGroup(invitecode: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthInstance.api.joinGroup(invitecode).awaitResponse()

                if (response.isSuccessful) {
                    Toast.makeText(context, "Joined successfully", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Toast.makeText(context, "HTTP Exception: ${e.message} ", Toast.LENGTH_SHORT).show()
            } catch (e: Throwable) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun userGroupDetail() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = AuthInstance.api.userGrpDetails().awaitResponse()

                if (response.isSuccessful && response.body() != null) {
                    response.body()?.data?.let { details: List<Data> ->
                        tripTrackAdapter.updateRecords(details)
                    }
                }
            } catch (e: Throwable) {
                Toast.makeText(context, " ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}