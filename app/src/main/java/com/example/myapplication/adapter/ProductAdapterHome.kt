package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemProductHomeBinding
import com.example.myapplication.model.Product
import kotlin.random.Random

class ProductAdapterHome(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapterHome.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductHomeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductHomeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            // Set data dasar
            tvProductName.text = product.name
            rbRating.rating = product.rating
            tvRatingValue.text = product.rating.toString()
            ivProductImage.setImageResource(product.imageRes)

            // ===== LOGIKA DISKON =====
            // Random diskon antara 10-40% untuk produk dengan sold > 1000
            val hasDiscount = product.soldCount > 1000 && Random.nextBoolean()
            val discountPercent = if (hasDiscount) {
                (10..40).random()
            } else {
                0
            }

            if (hasDiscount && discountPercent > 0) {
                val discountedPrice = product.price * (100 - discountPercent) / 100
                
                // Tampilkan harga diskon
                tvProductPrice.text = "Rp ${String.format("%,.0f", discountedPrice)}"
                
                // Tampilkan harga asli dengan coret
                tvOriginalPrice.visibility = View.VISIBLE
                tvOriginalPrice.text = "Rp ${String.format("%,.0f", product.price)}"
                
                // Tampilkan badge diskon
                tvDiscountBadge.visibility = View.VISIBLE
                tvDiscountBadge.text = "-$discountPercent%"
            } else {
                // Tidak ada diskon
                tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"
                tvOriginalPrice.visibility = View.GONE
                tvDiscountBadge.visibility = View.GONE
            }

            // ===== BADGE TERLARIS =====
            if (product.soldCount > 3000) {
                tvBadge.visibility = View.VISIBLE
                tvBadge.text = "🔥 Terlaris"
            } else {
                tvBadge.visibility = View.GONE
            }

            // ===== TERJUAL =====
            tvSoldCount.text = "Terjual ${product.soldCount}+"

            // ===== KLIK ITEM =====
            root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun getItemCount(): Int = products.size
}