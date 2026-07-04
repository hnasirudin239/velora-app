package com.example.myapplication.model

import java.io.Serializable

data class OrderDetail(
    val id: String,
    val date: String,
    val time: String,
    val status: String, // "pending", "shipped", "done", "cancelled"
    val statusLabel: String,
    val statusColor: Int,
    val items: List<OrderItem>,
    val subtotal: Double,
    val shippingCost: Double,
    val paymentMethod: String,
    val paymentMethodIcon: Int,
    val total: Double,
    val addressName: String,
    val addressDetail: String,
    val addressPhone: String,
    val courier: String,
    val trackingNumber: String,
    val timeline: List<TimelineStep>
) : Serializable

data class TimelineStep(
    val label: String,
    val isCompleted: Boolean,
    val isActive: Boolean
) : Serializable