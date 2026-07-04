package com.example.myapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "wishlist_items")
data class WishlistItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val productName: String,
    val productPrice: Double,
    val productImageRes: Int,
    val rating: Float,
    val soldCount: Int,
    val description: String = ""
) : Serializable