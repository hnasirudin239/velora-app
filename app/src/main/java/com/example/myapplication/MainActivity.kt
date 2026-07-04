package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.fragments.CartFragment
import com.example.myapplication.fragments.HomeFragment
import com.example.myapplication.fragments.ProfileFragment
import com.example.myapplication.fragments.SearchFragment
import com.example.myapplication.fragments.WishlistFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        loadFragment(HomeFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_wishlist -> {
                    loadFragment(WishlistFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // ✅ Fungsi publik untuk navigasi dari Fragment
    fun navigateToHome() {
        binding.bottomNavigation.selectedItemId = R.id.nav_home
        loadFragment(HomeFragment())
    }

    fun navigateToSearch() {
        binding.bottomNavigation.selectedItemId = R.id.nav_search
        loadFragment(SearchFragment())
    }

    fun navigateToCart() {
        binding.bottomNavigation.selectedItemId = R.id.nav_cart
        loadFragment(CartFragment())
    }

    fun navigateToWishlist() {
        binding.bottomNavigation.selectedItemId = R.id.nav_wishlist
        loadFragment(WishlistFragment())
    }

    fun navigateToProfile() {
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        loadFragment(ProfileFragment())
    }
}