package com.example.myapplication.model

import java.io.Serializable

data class OrderConfirmation(
    val orderId: String,
    val paymentMethod: String,
    val paymentStatus: String,
    val addressName: String,
    val addressDetail: String,
    val totalAmount: Double,
    val pointsEarned: Int = 0,
    val estimatedDelivery: String = "2 - 3 hari kerja"
) : Serializable