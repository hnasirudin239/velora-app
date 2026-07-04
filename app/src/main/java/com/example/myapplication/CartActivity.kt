package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.CartAdapter
import com.example.myapplication.data.database.CartDatabase
import com.example.myapplication.data.repository.CartRepository
import com.example.myapplication.databinding.ActivityCartBinding
import com.example.myapplication.model.CartItem  // ✅ Tambahkan import ini
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var cartRepository: CartRepository
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = CartDatabase.getInstance(this)
        cartRepository = CartRepository(database.cartDao())

        setupRecyclerView()
        setupClickListeners()
        observeCartData()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(
            onQuantityChanged = { cartItem, newQuantity ->
                updateQuantity(cartItem, newQuantity)
            },
            onDelete = { cartItem ->
                deleteItem(cartItem)
            }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnCheckout.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }

            lifecycleScope.launch {
                val total = cartRepository.getTotalPrice()
                if (total > 0) {
                    Toast.makeText(
                        this@CartActivity,
                        "💳 Total Rp ${String.format("%,.0f", total)} berhasil di-checkout!",
                        Toast.LENGTH_LONG
                    ).show()
                    cartRepository.clearCart()
                } else {
                    Toast.makeText(
                        this@CartActivity,
                        "🛒 Keranjang kosong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.btnClearCart.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }

            lifecycleScope.launch {
                cartRepository.clearCart()
                Toast.makeText(
                    this@CartActivity,
                    "🗑️ Keranjang dikosongkan",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeCartData() {
        lifecycleScope.launch {
            cartRepository.getAllCartItems().collect { cartItems ->
                adapter.submitList(cartItems)
                val total = cartRepository.getTotalPrice()
                binding.tvTotalPrice.text = "Rp ${String.format("%,.0f", total)}"

                if (cartItems.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.btnClearCart.visibility = View.GONE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    binding.btnClearCart.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        lifecycleScope.launch {
            cartRepository.updateQuantity(cartItem, newQuantity)
        }
    }

    private fun deleteItem(cartItem: CartItem) {
        lifecycleScope.launch {
            cartRepository.removeFromCart(cartItem)
            Toast.makeText(
                this@CartActivity,
                "🗑️ ${cartItem.productName} dihapus dari keranjang",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}