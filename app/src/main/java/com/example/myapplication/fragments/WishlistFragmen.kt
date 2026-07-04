package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.adapter.WishlistAdapterNew  // ← Impor adapter baru
import com.example.myapplication.data.database.CartDatabase
import com.example.myapplication.data.repository.CartRepository
import com.example.myapplication.data.repository.WishlistRepository
import com.example.myapplication.databinding.FragmentWishlistBinding
import com.example.myapplication.model.WishlistItem
import kotlinx.coroutines.launch

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private lateinit var wishlistRepository: WishlistRepository
    private lateinit var cartRepository: CartRepository
    private lateinit var adapter: WishlistAdapterNew  // ← Gunakan adapter baru

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = CartDatabase.getInstance(requireContext())
        wishlistRepository = WishlistRepository(database.wishlistDao())
        cartRepository = CartRepository(database.cartDao())

        setupRecyclerView()
        setupListeners()
        observeWishlistData()
    }

    private fun setupRecyclerView() {
        adapter = WishlistAdapterNew(
            onDeleteClick = { item ->
                lifecycleScope.launch {
                    wishlistRepository.removeFromWishlist(item.productId)
                    Toast.makeText(
                        requireContext(),
                        "🗑️ ${item.productName} dihapus dari wishlist",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            onAddToCartClick = { item ->
                lifecycleScope.launch {
                    try {
                        cartRepository.addToCart(
                            productId = item.productId,
                            productName = item.productName,
                            productPrice = item.productPrice,
                            productImageRes = item.productImageRes
                        )
                        Toast.makeText(
                            requireContext(),
                            "✅ ${item.productName} ditambahkan ke keranjang!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            requireContext(),
                            "❌ Gagal menambahkan ke keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            onItemClick = { item ->
                navigateToDetail(item)
            }
        )
        binding.rvWishlist.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvWishlist.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnShopNow.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }
            (activity as? MainActivity)?.navigateToHome()
        }
    }

    private fun observeWishlistData() {
        lifecycleScope.launch {
            wishlistRepository.getAllWishlistItems().collect { wishlistItems ->
                adapter.submitList(wishlistItems)
                binding.tvItemCount.text = "${wishlistItems.size} Item"

                if (wishlistItems.isEmpty()) {
                    binding.emptyLayout.visibility = View.VISIBLE
                    binding.rvWishlist.visibility = View.GONE
                } else {
                    binding.emptyLayout.visibility = View.GONE
                    binding.rvWishlist.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToDetail(item: WishlistItem) {
        val product = com.example.myapplication.model.Product(
            id = item.productId,
            name = item.productName,
            price = item.productPrice,
            imageRes = item.productImageRes,
            rating = item.rating,
            soldCount = item.soldCount,
            description = item.description
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