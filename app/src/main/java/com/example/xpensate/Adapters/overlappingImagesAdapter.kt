package com.example.xpensate.Adapters

import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.R

class OverlappingImagesAdapter(private val images: List<Any>) : RecyclerView.Adapter<OverlappingImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ImageView) : RecyclerView.ViewHolder(binding)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(80, 80).apply {
                marginStart = -25
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
            background = ContextCompat.getDrawable(context, R.drawable.circle_background)
            clipToOutline = true
        }
        return ImageViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResource = images[position]
        try {
            holder.binding.setImageResource(imageResource as Int)
        } catch (e: Exception) {
            holder.binding.setImageResource(R.drawable.avatar3)
        }    }

    override fun getItemCount(): Int = images.size
}
