package com.example.myapplication.model

import java.io.Serializable

data class OrderHistory(
    val id: String,
    val date: String,
    val time: String,
    val status: String, // "pending", "shipped", "done", "cancelled"
    val statusLabel: String,
    val items: List<OrderItem>,
    val totalPrice: Double
) : Serializable

data class OrderItem(
    val productName: String,
    val quantity: Int,
    val price: Double,
    val imageRes: Int
) : Serializable