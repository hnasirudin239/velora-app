package com.example.myapplication.model

import java.io.Serializable

data class CheckoutData(
    val cartItems: List<CartItem>,
    val subtotal: Double,
    val selectedVoucher: Voucher? = null,
    val pointsUsed: Int = 0,
    val pointsValue: Double = 0.0, // Nilai rupiah dari poin yang digunakan
    val shippingCost: Double,
    val discountAmount: Double = 0.0,
    val total: Double
) : Serializable

data class CheckoutSummary(
    val subtotal: Double,
    val voucherDiscount: Double,
    val pointsDiscount: Double,
    val shippingCost: Double,
    val total: Double,
    val pointsEarned: Int // Poin yang akan didapat dari transaksi ini
)