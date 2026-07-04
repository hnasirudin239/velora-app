package com.example.myapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout  // ✅ Tambahkan import ini
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityCheckoutSuccessBinding
import com.example.myapplication.model.OrderConfirmation
import java.text.NumberFormat
import java.util.Locale

class CheckoutSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutSuccessBinding
    private lateinit var confirmation: OrderConfirmation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        confirmation = intent.getSerializableExtra("confirmation") as? OrderConfirmation
            ?: getDummyConfirmation()

       setupViews()
        setupListeners()
        animateSuccessIcon()
    }

    private fun setupViews() {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))

        binding.tvOrderId.text = confirmation.orderId
        binding.tvPaymentMethod.text = confirmation.paymentMethod
        binding.tvPaymentStatus.text = confirmation.paymentStatus
        binding.tvAddressName.text = confirmation.addressName
        binding.tvAddressDetail.text = confirmation.addressDetail
        binding.tvTotalAmount.text = "Rp ${formatter.format(confirmation.totalAmount)}"

        // Points earned
        if (confirmation.pointsEarned > 0) {
            binding.tvPointsEarned.visibility = android.view.View.VISIBLE
            binding.tvPointsEarned.text = "⭐ Mendapat +${confirmation.pointsEarned} poin dari transaksi ini"
        } else {
            binding.tvPointsEarned.visibility = android.view.View.GONE
        }
    }

    private fun setupListeners() {
        // Copy Order ID
        binding.tvCopyOrder.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Order ID", confirmation.orderId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "✅ Nomor pesanan disalin!", Toast.LENGTH_SHORT).show()
        }

        // Lihat Status Pesanan
        binding.btnViewOrder.setOnClickListener { view ->
            animateView(view)
            val intent = Intent(this, OrderHistoryActivity::class.java)
            intent.putExtra("orderId", confirmation.orderId)
            startActivity(intent)
            finish()
        }

        // Kembali Beranda
        binding.btnBackHome.setOnClickListener { view ->
            animateView(view)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    // ✅ Perbaiki animasi icon sukses
    private fun animateSuccessIcon() {
        val iconWrapper = binding.successIconWrapper
        iconWrapper.scaleX = 0f
        iconWrapper.scaleY = 0f
        iconWrapper.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(android.view.animation.BounceInterpolator())
            .start()
    } 

    private fun animateView(view: android.view.View) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        }.start()
    }

    private fun getDummyConfirmation(): OrderConfirmation {
        return OrderConfirmation(
            orderId = "#INV-20260701-009",
            paymentMethod = "Virtual Account BCA",
            paymentStatus = "Lunas",
            addressName = "Andi Saputra",
            addressDetail = "Jl. Merdeka Raya No. 45, Menteng, Jakarta Pusat",
            totalAmount = 1742000.0,
            pointsEarned = 50
        )
    }
}