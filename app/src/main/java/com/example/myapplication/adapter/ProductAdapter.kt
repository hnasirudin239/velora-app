package com.example.myapplication.adapter  // ← Ubah package

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemProductBinding
import com.example.myapplication.model.Product  // ← Import Product

class ProductAdapter(
    private val products: List<Product>,
    private val context: Context,
    private val onItemClick: (View, Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.binding.apply {
            tvName.text = product.name
            tvPrice.text = "Rp ${String.format("%,.0f", product.price)}"
            tvSold.text = "🔥 Terjual ${product.soldCount}+"
            rbRating.rating = product.rating
            ivProduct.setImageResource(product.imageRes)
            ivProduct.transitionName = "productImage"

            btnAddCart.setOnClickListener { view ->
                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction {
                    view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                }
                Toast.makeText(context, "✅ ${product.name} masuk keranjang!", Toast.LENGTH_SHORT).show()
            }

            root.setOnClickListener {
                onItemClick(ivProduct, product)
            }
        }
    }

    override fun getItemCount(): Int = products.size
}