package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.WishlistItem

@Database(
    entities = [CartItem::class, WishlistItem::class],  // ← Tambahkan WishlistItem
    version = 2,  // ← Update version
    exportSchema = false
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao  // ← Tambahkan ini

    companion object {
        @Volatile
        private var INSTANCE: CartDatabase? = null

        fun getInstance(context: Context): CartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java,
                    "cart_database"
                )
                .fallbackToDestructiveMigration()  // ← Untuk migrasi versi
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}