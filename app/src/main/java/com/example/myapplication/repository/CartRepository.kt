package com.example.myapplication.data.repository

import com.example.myapplication.data.database.CartDao
import com.example.myapplication.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val cartDao: CartDao) {
    
    fun getAllCartItems(): Flow<List<CartItem>> = cartDao.getAllCartItems()
    
    suspend fun addToCart(productId: Int, productName: String, productPrice: Double, productImageRes: Int) {
        withContext(Dispatchers.IO) {
            val existingItem = cartDao.getCartItemByProductId(productId)
            if (existingItem != null) {
                val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
                cartDao.updateCartItem(updatedItem)
            } else {
                val newItem = CartItem(
                    productId = productId,
                    productName = productName,
                    productPrice = productPrice,
                    productImageRes = productImageRes,
                    quantity = 1
                )
                cartDao.insertCartItem(newItem)
            }
        }
    }
    
    suspend fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        withContext(Dispatchers.IO) {
            if (newQuantity <= 0) {
                cartDao.deleteCartItemById(cartItem.id)
            } else {
                val updatedItem = cartItem.copy(quantity = newQuantity)
                cartDao.updateCartItem(updatedItem)
            }
        }
    }
    
    suspend fun removeFromCart(cartItem: CartItem) {
        withContext(Dispatchers.IO) {
            cartDao.deleteCartItemById(cartItem.id)
        }
    }
    
    suspend fun clearCart() {
        withContext(Dispatchers.IO) {
            cartDao.clearCart()
        }
    }
    
    suspend fun getTotalPrice(): Double {
        return withContext(Dispatchers.IO) {
            cartDao.getTotalPrice() ?: 0.0
        }
    }

    // Di CartRepository.kt
suspend fun getTotalItemCount(): Int {
    val items = cartDao.getAllCartItems()
    // Karena Flow, kita perlu collect
    var count = 0
    // Gunakan cara ini jika perlu
    return count
}
}