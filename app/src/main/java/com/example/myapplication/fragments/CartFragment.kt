package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.CheckoutActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.CartAdapterNew
import com.example.myapplication.data.database.CartDatabase
import com.example.myapplication.data.repository.CartRepository
import com.example.myapplication.databinding.FragmentCartBinding
import com.example.myapplication.model.CartItem
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartRepository: CartRepository
    private lateinit var adapter: CartAdapterNew

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Repository
        val database = CartDatabase.getInstance(requireContext())
        cartRepository = CartRepository(database.cartDao())

        setupRecyclerView()
        setupListeners()
        observeCartData()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapterNew(
            onQuantityChanged = { cartItem, newQuantity ->
                updateQuantity(cartItem, newQuantity)
            },
            onDelete = { cartItem ->
                deleteItem(cartItem)
            },
            onItemClick = { cartItem ->
                navigateToDetail(cartItem)
            }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = adapter
    }

    private fun setupListeners() {
        // Tombol Back
        binding.ivBack.setOnClickListener {
            (activity as? MainActivity)?.navigateToHome()
        }

        // Tombol Kosongkan
        binding.btnClearCart.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            lifecycleScope.launch {
                cartRepository.clearCart()
                Toast.makeText(requireContext(), "🗑️ Keranjang dikosongkan", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Checkout
        binding.btnCheckout.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            
            // ✅ Navigasi ke CheckoutActivity
            val intent = android.content.Intent(requireContext(), com.example.myapplication.CheckoutActivity::class.java)
            startActivity(intent)
        }

        // Tombol Belanja Sekarang (Empty State)
        binding.btnShopNow.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            (activity as? MainActivity)?.navigateToHome()
        }
    }

    private fun observeCartData() {
        lifecycleScope.launch {
            cartRepository.getAllCartItems().collect { cartItems ->
                adapter.submitList(cartItems)
                val total = cartRepository.getTotalPrice()
                binding.tvTotalPrice.text = "Rp ${String.format("%,.0f", total)}"

                if (cartItems.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                    binding.bottomBar.visibility = View.GONE
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    binding.bottomBar.visibility = View.VISIBLE
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
                requireContext(),
                "🗑️ ${cartItem.productName} dihapus dari keranjang",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun navigateToDetail(cartItem: CartItem) {
        val product = com.example.myapplication.model.Product(
            id = cartItem.productId,
            name = cartItem.productName,
            price = cartItem.productPrice,
            imageRes = cartItem.productImageRes,
            rating = 4.5f,
            soldCount = 0,
            description = ""
        )
        val intent = android.content.Intent(requireContext(), com.example.myapplication.ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}