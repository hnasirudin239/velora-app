package com.example.myapplication.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.model.WishlistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    
    @Query("SELECT * FROM wishlist_items")
    fun getAllWishlistItems(): Flow<List<WishlistItem>>
    
    @Query("SELECT * FROM wishlist_items WHERE productId = :productId LIMIT 1")
    suspend fun getWishlistItemByProductId(productId: Int): WishlistItem?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlistItem(item: WishlistItem)
    
    @Delete
    suspend fun deleteWishlistItem(item: WishlistItem)
    
    @Query("DELETE FROM wishlist_items WHERE productId = :productId")
    suspend fun deleteWishlistItemByProductId(productId: Int)
    
    @Query("DELETE FROM wishlist_items")
    suspend fun clearWishlist()
    
    @Query("SELECT COUNT(*) FROM wishlist_items")
    suspend fun getWishlistCount(): Int
}