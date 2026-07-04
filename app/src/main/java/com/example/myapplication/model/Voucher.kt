package com.example.myapplication.model

data class Voucher(
    val id: Int,
    val type: String, // "money", "percent", "shipping"
    val discountAmount: String, // "Rp 50K", "20%", "Gratis"
    val discountLabel: String, // "Diskon Belanja", "Potongan", "Ongkir"
    val minSpend: String, // "Min. 250K", "Maks. 50K", "Min. 100K"
    val title: String,
    val code: String,
    val expiryDate: String,
    val status: String // "active", "used", "expired"
)