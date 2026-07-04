package com.example.myapplication.data.repository

import com.example.myapplication.data.database.WishlistDao
import com.example.myapplication.model.WishlistItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WishlistRepository(private val wishlistDao: WishlistDao) {
    
    fun getAllWishlistItems(): Flow<List<WishlistItem>> = wishlistDao.getAllWishlistItems()
    
    suspend fun addToWishlist(productId: Int, productName: String, productPrice: Double, 
                              productImageRes: Int, rating: Float, soldCount: Int, description: String = "") {
        withContext(Dispatchers.IO) {
            val existingItem = wishlistDao.getWishlistItemByProductId(productId)
            if (existingItem == null) {
                val newItem = WishlistItem(
                    productId = productId,
                    productName = productName,
                    productPrice = productPrice,
                    productImageRes = productImageRes,
                    rating = rating,
                    soldCount = soldCount,
                    description = description
                )
                wishlistDao.insertWishlistItem(newItem)
            }
        }
    }
    
    suspend fun removeFromWishlist(productId: Int) {
        withContext(Dispatchers.IO) {
            wishlistDao.deleteWishlistItemByProductId(productId)
        }
    }
    
    suspend fun clearWishlist() {
        withContext(Dispatchers.IO) {
            wishlistDao.clearWishlist()
        }
    }
    
    suspend fun isInWishlist(productId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            wishlistDao.getWishlistItemByProductId(productId) != null
        }
    }
    
    suspend fun getWishlistCount(): Int {
        return withContext(Dispatchers.IO) {
            wishlistDao.getWishlistCount()
        }
    }
}