package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemPromoBinding

class CarouselAdapter(
    private val images: List<Int>
) : RecyclerView.Adapter<CarouselAdapter.PromoViewHolder>() {

    class PromoViewHolder(val binding: ItemPromoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromoViewHolder {
        val binding = ItemPromoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PromoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PromoViewHolder, position: Int) {
        holder.binding.ivPromo.setImageResource(images[position])
    }

    override fun getItemCount(): Int = images.size
}