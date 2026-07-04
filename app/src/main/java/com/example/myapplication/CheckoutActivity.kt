package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.OrderSummaryAdapter
import com.example.myapplication.data.database.CartDatabase
import com.example.myapplication.data.repository.CartRepository
import com.example.myapplication.databinding.ActivityCheckoutBinding
import com.example.myapplication.model.Voucher
import com.example.myapplication.viewmodel.CheckoutViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat
import com.example.myapplication.CheckoutSuccessActivity
import com.example.myapplication.model.OrderConfirmation
import java.util.Date


class CheckoutActivity : AppCompatActivity() {

    private lateinit var cartRepository: CartRepository
    private lateinit var orderAdapter: OrderSummaryAdapter
    private var selectedShipping = "Reguler"
    private var selectedPayment = "Transfer Bank"
    private val shippingCostReguler = 12000
    private val shippingCostExpress = 30000

    private lateinit var binding: ActivityCheckoutBinding
    private val viewModel: CheckoutViewModel by viewModels()
    private var selectedVoucher: Voucher? = null
    private var isVoucherApplied = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = CartDatabase.getInstance(this)
        cartRepository = CartRepository(database.cartDao())

        setupRecyclerView()
        setupListeners()
        observeViewModel()
        loadCartData()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderSummaryAdapter(emptyList())
        binding.rvOrderItems.layoutManager = LinearLayoutManager(this)
        binding.rvOrderItems.adapter = orderAdapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        // ✅ Voucher Picker with BottomSheetDialog
        binding.llSelectVoucher.setOnClickListener { view ->
            animateView(view)
            showVoucherPickerDialog()
        }

        // Points
        binding.btnPointsMinus.setOnClickListener { view ->
            animateView(view)
            viewModel.decreasePoints()
        }

        binding.btnPointsPlus.setOnClickListener { view ->
            animateView(view)
            viewModel.increasePoints()
        }

        binding.btnPlaceOrder.setOnClickListener { view ->
            animateView(view)
            placeOrder()
        }

        // Edit Address
        binding.llEditAddress.setOnClickListener { view ->
            animateView(view)
            startActivity(Intent(this, AddressActivity::class.java))
        }

        // Shipping
        binding.llShippingReguler.setOnClickListener { view ->
            selectShipping(view, binding.radioShippingReguler, "Reguler")
        }

        binding.llShippingExpress.setOnClickListener { view ->
            selectShipping(view, binding.radioShippingExpress, "Express")
        }

        // Payment
        binding.llPaymentBank.setOnClickListener { view ->
            selectPayment(view, binding.radioPaymentBank, "Transfer Bank")
        }

        binding.llPaymentEwallet.setOnClickListener { view ->
            selectPayment(view, binding.radioPaymentEwallet, "E-Wallet")
        }
    }

    private fun showVoucherPickerDialog() {
        val vouchers = listOf(
            Voucher(1, "money", "Rp 50K", "Diskon Belanja", "Min. 250K",
                "Voucher Belanja Akhir Bulan", "NEURO50K", "15 Jul 2026", "active"),
            Voucher(2, "percent", "20%", "Potongan", "Maks. 50K",
                "Diskon Spesial Pengguna Baru", "NEURO20", "10 Jul 2026", "active"),
            Voucher(3, "shipping", "Gratis", "Ongkir", "Min. 100K",
                "Bebas Ongkir All Area", "FREESHIP", "20 Jun 2026", "expired")
        )

        // Filter voucher yang aktif saja
        val activeVouchers = vouchers.filter { it.status == "active" }

        if (activeVouchers.isEmpty()) {
            Toast.makeText(this, "Tidak ada voucher tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Buat dialog dengan callback yang jelas
        val dialog = VoucherPickerDialog.newInstance(activeVouchers) { selected ->
            // ✅ Callback ini akan dipanggil saat user memilih atau membatalkan
            if (selected != null) {
                selectedVoucher = selected
                isVoucherApplied = true
                binding.tvSelectedVoucher.text = "${selected.title} (${selected.discountAmount})"
                binding.tvVoucherDiscount.visibility = View.VISIBLE
                binding.tvVoucherDiscount.text = "Diskon: ${selected.discountAmount}"
                viewModel.setVoucher(selected)
                //Toast.makeText(this, "✅ Voucher ${selected.code} dipilih", Toast.LENGTH_SHORT).show()
            } else {
                // Batal pilih
                selectedVoucher = null
                isVoucherApplied = false
                binding.tvSelectedVoucher.text = "Pilih Voucher"
                binding.tvVoucherDiscount.visibility = View.GONE
                viewModel.setVoucher(null)
                //Toast.makeText(this, "Voucher dibatalkan", Toast.LENGTH_SHORT).show()
            }
        }
        
        // ✅ Tampilkan dialog
        dialog.show(supportFragmentManager, "VoucherPickerDialog")
    }


    private fun observeViewModel() {
        viewModel.subtotal.observe(this) { subtotal ->
            binding.tvSubtotal.text = formatRupiah(subtotal)
        }

        viewModel.voucherDiscount.observe(this) { discount ->
            binding.tvVoucherDiscountTotal.text = "- ${formatRupiah(discount)}"
            binding.tvVoucherDiscount.text = "Diskon: ${formatRupiah(discount)}"
        }

        viewModel.pointsDiscount.observe(this) { discount ->
            binding.tvPointsDiscountTotal.text = "- ${formatRupiah(discount)}"
            binding.tvPointsValue.text = "Nilai poin: ${formatRupiah(discount)}"
        }

        viewModel.pointsToUse.observe(this) { points ->
            binding.tvPointsToUse.text = points.toString()
        }

        viewModel.pointsBalance.observe(this) { balance ->
            binding.tvPointsBalance.text = "Poin tersedia: $balance"
        }

        viewModel.total.observe(this) { total ->
            binding.tvGrandTotal.text = formatRupiah(total)
        }

        viewModel.pointsEarned.observe(this) { earned ->
            binding.tvPointsEarned.text = "⭐ Akan dapat +$earned poin"
        }

        viewModel.shippingCost.observe(this) { shipping ->
            binding.tvShipping.text = formatRupiah(shipping)
        }
    }

    private fun placeOrder() {
        val summary = viewModel.getCheckoutSummary()
        val cartItems = viewModel.cartItems.value ?: emptyList()

        // Buat Order ID
        val orderId = generateOrderId()
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date())

        // Buat OrderConfirmation
        val confirmation = OrderConfirmation(
            orderId = orderId,
            paymentMethod = when (selectedPayment) {
                "Transfer Bank" -> "Virtual Account BCA"
                "E-Wallet" -> "GoPay / OVO / Dana"
                else -> "Transfer Bank (BCA)"
            },
            paymentStatus = "Lunas",
            addressName = "Andi Saputra", // Dari alamat yang dipilih
            addressDetail = "Jl. Merdeka Raya No. 45, Menteng, Jakarta Pusat",
            totalAmount = summary.total,
            pointsEarned = summary.pointsEarned
        )

        // Simpan ke database (order history) - kode sebelumnya

        // Navigasi ke CheckoutSuccessActivity
        val intent = Intent(this, CheckoutSuccessActivity::class.java)
        intent.putExtra("confirmation", confirmation)
        startActivity(intent)
        finish()
    }

    private fun selectShipping(view: View, radioView: View, type: String) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        animateView(view)

        selectedShipping = type
        resetShippingSelection()

        view.background = getDrawable(R.drawable.bg_radio_selected)
        radioView.background = getDrawable(R.drawable.radio_selected)

        viewModel.setShippingCost(if (type == "Reguler") shippingCostReguler.toDouble() else shippingCostExpress.toDouble())
        updateGrandTotal()
    }

    private fun resetShippingSelection() {
        binding.llShippingReguler.background = getDrawable(R.drawable.bg_radio_unselected)
        binding.llShippingExpress.background = getDrawable(R.drawable.bg_radio_unselected)
        binding.radioShippingReguler.background = getDrawable(R.drawable.radio_unselected)
        binding.radioShippingExpress.background = getDrawable(R.drawable.radio_unselected)
    }

    private fun selectPayment(view: View, radioView: View, type: String) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        animateView(view)

        selectedPayment = type
        resetPaymentSelection()

        view.background = getDrawable(R.drawable.bg_radio_selected)
        radioView.background = getDrawable(R.drawable.radio_selected)
    }

    private fun resetPaymentSelection() {
        binding.llPaymentBank.background = getDrawable(R.drawable.bg_radio_unselected)
        binding.llPaymentEwallet.background = getDrawable(R.drawable.bg_radio_unselected)
        binding.radioPaymentBank.background = getDrawable(R.drawable.radio_unselected)
        binding.radioPaymentEwallet.background = getDrawable(R.drawable.radio_unselected)
    }

    private fun loadCartData() {
        lifecycleScope.launch {
            cartRepository.getAllCartItems().collect { cartItems ->
                orderAdapter = OrderSummaryAdapter(cartItems)
                binding.rvOrderItems.adapter = orderAdapter

                val total = cartRepository.getTotalPrice()
                val totalItems = cartItems.sumOf { it.quantity }
                binding.tvTotalItemsLabel.text = "Total Barang ($totalItems item)"
                binding.tvTotalItemsPrice.text = formatRupiah(total)
                binding.tvGrandTotal.text = formatRupiah(total + shippingCostReguler)
            }
        }
    }

    private fun updateGrandTotal() {
        lifecycleScope.launch {
            val total = cartRepository.getTotalPrice()
            val shippingCost = if (selectedShipping == "Reguler") shippingCostReguler else shippingCostExpress
            binding.tvGrandTotal.text = formatRupiah(total + shippingCost)
        }
    }

    private fun generateOrderId(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale("id", "ID"))
        val date = dateFormat.format(Date()) // ✅ gunakan format(Date())
        val random = (100..999).random()
        return "#INV-$date-$random"
    }

    private fun formatRupiah(value: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp ${formatter.format(value)}"
    }

    private fun animateView(view: android.view.View) {
        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
        view.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
        }.start()
    }
}