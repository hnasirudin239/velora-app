package com.example.myapplication.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    
    // GET ALL - pakai Flow
    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>
    
    // GET BY PRODUCT ID - tanpa suspend (blocking)
    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    fun getCartItemByProductId(productId: Int): CartItem?
    
    // INSERT - return void (paling aman)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)
    
    // UPDATE - return void (paling aman)
    @Update
    suspend fun updateCartItem(cartItem: CartItem)
    
    // DELETE - return void (paling aman)
    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteCartItemById(itemId: Int)
    
    // CLEAR ALL - return void (paling aman)
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
    
    // GET TOTAL PRICE - return Double (nullable)
    @Query("SELECT SUM(productPrice * quantity) FROM cart_items")
    suspend fun getTotalPrice(): Double?
}