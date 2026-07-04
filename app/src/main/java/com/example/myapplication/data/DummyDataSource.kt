package com.example.myapplication.data  // ← Ubah package

import com.example.myapplication.R
import com.example.myapplication.model.Product  // ← Import Product

object DummyDataSource {
    fun getProducts(): List<Product> = listOf(
        Product(1, "Sepatu Lari Aerox", 450000.0, R.drawable.ic_launcher_foreground, 4.8f, 3245),
        Product(2, "Jam Tangan Smart X", 1250000.0, R.drawable.ic_launcher_foreground, 4.9f, 1289),
        Product(3, "Headphone Bass Pro", 750000.0, R.drawable.ic_launcher_foreground, 4.7f, 872),
        Product(4, "Tas Ransel Anti Air", 320000.0, R.drawable.ic_launcher_foreground, 4.5f, 5400),
    )
}