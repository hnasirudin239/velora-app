package com.example.myapplication.adapter

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemProductThumbnailBinding

class ProductThumbnailAdapter(
    private val images: List<Int>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<ProductThumbnailAdapter.ThumbnailViewHolder>() {

    private var selectedPosition = 0

    class ThumbnailViewHolder(val binding: ItemProductThumbnailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val binding = ItemProductThumbnailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ThumbnailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        holder.binding.ivThumbnail.setImageResource(images[position])

        // ✅ Perbaiki: strokeWidth menerima Float, bukan Int
        if (position == selectedPosition) {
            holder.binding.ivThumbnail.strokeColor = android.content.res.ColorStateList.valueOf(
                Color.parseColor("#1A73E8")
            )
            // Konversi 4dp ke pixel, lalu ke Float
            val strokeWidthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                holder.itemView.resources.displayMetrics
            )
            holder.binding.ivThumbnail.strokeWidth = strokeWidthPx // ✅ Float
        } else {
            holder.binding.ivThumbnail.strokeColor = android.content.res.ColorStateList.valueOf(
                Color.TRANSPARENT
            )
            holder.binding.ivThumbnail.strokeWidth = 0f // ✅ Float
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = images.size
}