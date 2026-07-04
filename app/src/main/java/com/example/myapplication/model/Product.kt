package com.example.myapplication.model  // ← Ubah dari tokoneuro ke myapplication

import java.io.Serializable

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageRes: Int,
    val rating: Float,
    val soldCount: Int,
    val description: String = "Produk berkualitas tinggi dengan desain modern dan material terbaik. Cocok untuk kebutuhan sehari-hari Anda."
) : Serializable