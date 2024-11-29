package com.example.xpensate.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.xpensate.API.home.CategoryList.CategoriesList
import com.example.xpensate.API.home.CategoryList.CategoriesListItem
import com.example.xpensate.R
import com.example.xpensate.databinding.CategoriesItemBinding

class CategoriesListAdapter(
    private val categoryList: List<CategoriesListItem>,
    private val imageMap: Map<String, Int>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoriesListAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: CategoriesItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoriesItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        with(holder.binding) {
            name.text = category.name

            val imageRes = imageMap[category.name] ?: R.drawable.other_icon
            image.setImageResource(imageRes)
            root.setOnClickListener {
                onCategoryClick(category.name)
            }
        }
    }

    override fun getItemCount(): Int = categoryList.size
}
