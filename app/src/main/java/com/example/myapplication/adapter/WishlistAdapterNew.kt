package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemWishlistNewBinding
import com.example.myapplication.model.WishlistItem

class WishlistAdapterNew(
    private val onDeleteClick: (WishlistItem) -> Unit,
    private val onAddToCartClick: (WishlistItem) -> Unit,
    private val onItemClick: (WishlistItem) -> Unit
) : RecyclerView.Adapter<WishlistAdapterNew.WishlistViewHolder>() {

    private var items = listOf<WishlistItem>()

    fun submitList(newItems: List<WishlistItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val binding = ItemWishlistNewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class WishlistViewHolder(
        private val binding: ItemWishlistNewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: WishlistItem) {
            binding.apply {
                tvProductName.text = item.productName
                tvProductPrice.text = "Rp ${String.format("%,.0f", item.productPrice)}"
                rbRating.rating = item.rating
                tvRatingValue.text = item.rating.toString()
                tvSoldCount.text = "(${item.soldCount}+)"
                ivProductImage.setImageResource(item.productImageRes)

                // Haptic + Animasi pada tombol hapus
                btnDelete.setOnClickListener { view ->
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }
                    onDeleteClick(item)
                }

                // Haptic + Animasi pada tombol tambah ke keranjang
                btnAddCart.setOnClickListener { view ->
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }
                    onAddToCartClick(item)
                }

                // Klik item untuk buka detail
                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }
}