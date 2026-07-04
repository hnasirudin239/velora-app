package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemCartNewBinding
import com.example.myapplication.model.CartItem

class CartAdapterNew(
    private val onQuantityChanged: (CartItem, Int) -> Unit,
    private val onDelete: (CartItem) -> Unit,
    private val onItemClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapterNew.CartViewHolder>() {

    private var items = listOf<CartItem>()

    fun submitList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartNewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(
        private val binding: ItemCartNewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.apply {
                tvCartName.text = item.productName
                tvCartPrice.text = "Rp ${String.format("%,.0f", item.productPrice)}"
                tvQuantity.text = item.quantity.toString()
                ivCartImage.setImageResource(item.productImageRes)

                // 🧠 NEURO: Haptic + Animasi pada tombol minus
                btnMinus.setOnClickListener { view ->
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }
                    if (item.quantity > 1) {
                        onQuantityChanged(item, item.quantity - 1)
                    }
                }

                // 🧠 NEURO: Haptic + Animasi pada tombol plus
                btnPlus.setOnClickListener { view ->
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }
                    onQuantityChanged(item, item.quantity + 1)
                }

                // 🧠 NEURO: Haptic + Animasi pada tombol hapus
                btnDelete.setOnClickListener { view ->
                    view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                    view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(80).withEndAction {
                        view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    }
                    onDelete(item)
                }

                // Klik item untuk buka detail
                root.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }
}