package com.example.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.MainActivity
import com.example.myapplication.data.database.CartDatabase
import com.example.myapplication.data.repository.WishlistRepository
import com.example.myapplication.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import com.example.myapplication.OrderHistoryActivity
import com.example.myapplication.AddressActivity
import com.example.myapplication.EditProfileActivity
import com.example.myapplication.LoginActivity
import com.example.myapplication.MyPointActivity
import com.example.myapplication.MyVoucherActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var wishlistRepository: WishlistRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Repository
        val database = CartDatabase.getInstance(requireContext())
        wishlistRepository = WishlistRepository(database.wishlistDao())

        // Load data
        loadUserData()
        observeWishlistCount()
        setupClickListeners()
    }

    private fun loadUserData() {
        binding.apply {
            tvUsername.text = "Pengguna Neuro"
            tvEmail.text = "user@tokoneuro.com"
            tvOrderCount.text = "12"
            tvPoints.text = "1.250"
        }
    }

    private fun observeWishlistCount() {
        lifecycleScope.launch {
            wishlistRepository.getAllWishlistItems().collect { wishlistItems ->
                val count = wishlistItems.size
                binding.tvWishlistCount.text = count.toString()
            }
        }
    }

    private fun setupClickListeners() {
        // Semua menu dengan haptic feedback dan animasi

        binding.llEditProfile.setOnClickListener { view ->
            animateView(view)
            Toast.makeText(requireContext(), "✏️ Buka Edit Profil", Toast.LENGTH_SHORT).show()
            startActivity(android.content.Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.llAddress.setOnClickListener { view ->
            animateView(view)
            Toast.makeText(requireContext(), "📍 Buka Alamat Pengiriman", Toast.LENGTH_SHORT).show()
            startActivity(android.content.Intent(requireContext(), AddressActivity::class.java))
        }

        binding.llPayment.setOnClickListener { view ->
            animateView(view)
            Toast.makeText(requireContext(), "💳 Buka Metode Pembayaran", Toast.LENGTH_SHORT).show()
        }

        binding.llOrderHistory.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), OrderHistoryActivity::class.java))
        }
        binding.llLogout.setOnClickListener { view ->
            animateView(view)
            showLogoutDialog()
        }

        binding.llPoints.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), MyPointActivity::class.java))
        }

        binding.llMypoint.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), MyPointActivity::class.java))
        }

        binding.llVoucher.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), MyVoucherActivity::class.java))
        }
    }

    private fun animateView(view: View) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        }.start()
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin logout?")
            .setPositiveButton("Ya") { _, _ ->
                // Navigasi ke LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                (activity as? MainActivity)?.finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}