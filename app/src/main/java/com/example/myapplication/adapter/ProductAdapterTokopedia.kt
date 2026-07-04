package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemProductTokopediaBinding
import com.example.myapplication.model.Product

class ProductAdapterTokopedia(
    private val products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapterTokopedia.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductTokopediaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductTokopediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = "Rp ${String.format("%,.0f", product.price)}"
            rbRating.rating = product.rating
            tvRatingValue.text = product.rating.toString()
            tvSoldCount.text = "Terjual ${product.soldCount}+"
            ivProductImage.setImageResource(product.imageRes)

            // Badge Terlaris
            if (product.soldCount > 3000) {
                tvBadge.visibility = View.VISIBLE
                tvBadge.text = "🔥 Terlaris"
            } else {
                tvBadge.visibility = View.GONE
            }

            // Tombol Beli
            btnAddToCart.setOnClickListener { view ->
                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                view.animate().scaleX(0.9f).scaleY(0.9f).setDuration(80).withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                }
                Toast.makeText(
                    view.context,
                    "✅ ${product.name} masuk keranjang!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            // Klik item
            root.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun getItemCount(): Int = products.size
}