package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.Voucher
import kotlinx.coroutines.launch
import com.example.myapplication.R
import com.example.myapplication.model.CheckoutData
import com.example.myapplication.model.CheckoutSummary

class CheckoutViewModel : ViewModel() {

    // Data
    private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _subtotal = MutableLiveData(0.0)
    val subtotal: LiveData<Double> = _subtotal

    private val _selectedVoucher = MutableLiveData<Voucher?>(null)
    val selectedVoucher: LiveData<Voucher?> = _selectedVoucher

    private val _pointsBalance = MutableLiveData(0)
    val pointsBalance: LiveData<Int> = _pointsBalance

    private val _pointsToUse = MutableLiveData(0)
    val pointsToUse: LiveData<Int> = _pointsToUse

    private val _shippingCost = MutableLiveData(12000.0)
    val shippingCost: LiveData<Double> = _shippingCost

    // Results
    private val _voucherDiscount = MutableLiveData(0.0)
    val voucherDiscount: LiveData<Double> = _voucherDiscount

    private val _pointsDiscount = MutableLiveData(0.0)
    val pointsDiscount: LiveData<Double> = _pointsDiscount

    private val _total = MutableLiveData(0.0)
    val total: LiveData<Double> = _total

    private val _pointsEarned = MutableLiveData(0)
    val pointsEarned: LiveData<Int> = _pointsEarned

    // Konfigurasi
    private val POINTS_TO_RUPIAH_RATE = 100 // 100 poin = Rp 1.000 (contoh: 1 poin = Rp 10)
    private val MAX_POINTS_PERCENTAGE = 0.3 // Maksimal 30% dari subtotal

    init {
        loadDummyData()
        calculateTotal()
    }

    private fun loadDummyData() {
        // Simulasi data dari cart dan poin
        val items = listOf(
            CartItem(1, 1, "Sepatu Lari Aerox", 450000.0, R.drawable.ic_launcher_foreground, 1),
            CartItem(2, 2, "Jam Tangan Smart X", 1250000.0, R.drawable.ic_launcher_foreground, 1)
        )
        _cartItems.value = items
        _subtotal.value = items.sumOf { it.productPrice * it.quantity }
        _pointsBalance.value = 1250 // Poin dari API
        _shippingCost.value = 12000.0
    }

    fun setVoucher(voucher: Voucher?) {
        _selectedVoucher.value = voucher
        calculateTotal()
    }

    fun setPointsToUse(points: Int) {
        val maxPoints = getMaxPointsAllowed()
        val validPoints = points.coerceIn(0, maxPoints)
        _pointsToUse.value = validPoints
        calculateTotal()
    }

    fun increasePoints() {
        val current = _pointsToUse.value ?: 0
        val step = 50
        val max = getMaxPointsAllowed()
        val newValue = (current + step).coerceAtMost(max)
        _pointsToUse.value = newValue
        calculateTotal()
    }

    fun decreasePoints() {
        val current = _pointsToUse.value ?: 0
        val step = 50
        val newValue = (current - step).coerceAtLeast(0)
        _pointsToUse.value = newValue
        calculateTotal()
    }

    private fun getMaxPointsAllowed(): Int {
        val subtotalValue = _subtotal.value ?: 0.0
        // Maksimal 30% dari subtotal dibagi nilai per poin
        val maxRupiah = subtotalValue * MAX_POINTS_PERCENTAGE
        val maxPoints = (maxRupiah / POINTS_TO_RUPIAH_RATE).toInt()
        val balance = _pointsBalance.value ?: 0
        return minOf(maxPoints, balance)
    }

    private fun calculateTotal() {
        val subtotalValue = _subtotal.value ?: 0.0
        val shipping = _shippingCost.value ?: 0.0

        // 1. Diskon Voucher
        val voucher = _selectedVoucher.value
        val voucherDiscountValue = when (voucher?.type) {
            "money" -> {
                // Parse "Rp 50K" -> 50000
                val amountStr = voucher.discountAmount.replace("Rp ", "").replace("K", "000")
                amountStr.toDoubleOrNull() ?: 0.0
            }
            "percent" -> {
                val percent = voucher.discountAmount.replace("%", "").toDoubleOrNull() ?: 0.0
                subtotalValue * (percent / 100)
            }
            "shipping" -> shipping // Gratis ongkir
            else -> 0.0
        }
        _voucherDiscount.value = voucherDiscountValue

        // 2. Potongan Poin
        val points = _pointsToUse.value ?: 0
        val pointsDiscountValue = points * POINTS_TO_RUPIAH_RATE.toDouble()
        _pointsDiscount.value = pointsDiscountValue

        // 3. Total
        val totalValue = subtotalValue - voucherDiscountValue - pointsDiscountValue + shipping
        _total.value = maxOf(totalValue, 0.0)

        // 4. Poin yang akan didapat (contoh: 1 poin per Rp 10.000)
        val earned = (subtotalValue / 10000).toInt()
        _pointsEarned.value = earned

        // Update UI values di Activity melalui callback
    }

    fun getCheckoutSummary(): CheckoutSummary {
        return CheckoutSummary(
            subtotal = _subtotal.value ?: 0.0,
            voucherDiscount = _voucherDiscount.value ?: 0.0,
            pointsDiscount = _pointsDiscount.value ?: 0.0,
            shippingCost = _shippingCost.value ?: 0.0,
            total = _total.value ?: 0.0,
            pointsEarned = _pointsEarned.value ?: 0
        )
    }

    fun placeOrder(): Boolean {
        // Simulasi place order
        // Di sini akan dipanggil API
        return true
    }

    fun setShippingCost(cost: Double) {
        _shippingCost.value = cost
        calculateTotal()
    }
}