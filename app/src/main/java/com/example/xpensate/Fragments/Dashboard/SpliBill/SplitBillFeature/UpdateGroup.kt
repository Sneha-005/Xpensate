package com.example.xpensate.Fragments.Dashboard.SpliBill.SplitBillFeature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.Adapters.OverlappingImagesAdapter
import com.example.xpensate.R

class UpdateGroup : Fragment() {

    private lateinit var overlappingImagesAdapter: OverlappingImagesAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_group, container, false)

        recyclerView = view.findViewById(R.id.overlapping_images_recycler)

        val sampleImages = listOf(
            R.drawable.avatar1,
            R.drawable.avatar3
        )

        overlappingImagesAdapter = OverlappingImagesAdapter(sampleImages)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = overlappingImagesAdapter
        }

        return view
    }
}
