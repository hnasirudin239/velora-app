package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.AddressAdapter
import com.example.myapplication.databinding.ActivityAddressBinding
import com.example.myapplication.model.Address
import android.content.Intent

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding
    private lateinit var adapter: AddressAdapter

    private val dummyAddresses = listOf(
        Address(
            id = 1,
            name = "Andi Saputra",
            phone = "+62 812-3456-7890",
            address = "Jl. Merdeka Raya No. 45, RT 05 RW 02, Kel. Menteng, Kec. Menteng\nJakarta Pusat, DKI Jakarta 10310",
            isDefault = true
        ),
        Address(
            id = 2,
            name = "Budi Santoso (Kantor)",
            phone = "+62 813-9876-5432",
            address = "Gedung Toko Neuro, Lantai 2, Jl. Sudirman No. 10, RT 01 RW 04\nKel. Senayan, Kec. Kebayoran Baru\nJakarta Selatan, DKI Jakarta 12190",
            isDefault = false
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        adapter = AddressAdapter(
            addresses = dummyAddresses,
            onEdit = { address ->
                Toast.makeText(this, "Edit: ${address.name}", Toast.LENGTH_SHORT).show()
                // Buka AddressFormActivity untuk edit
                startActivity(Intent(this, AddressFormActivity::class.java))
            },
            onDelete = { address ->
                Toast.makeText(this, "Hapus: ${address.name}", Toast.LENGTH_SHORT).show()
                // Tampilkan konfirmasi hapus
            },
            onItemClick = { address ->
                Toast.makeText(this, "Pilih: ${address.name}", Toast.LENGTH_SHORT).show()
                // Jika dipanggil dari Checkout, kirim hasil kembali
            }
        )
        binding.rvAddress.layoutManager = LinearLayoutManager(this)
        binding.rvAddress.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.fabAdd.setOnClickListener {
            Toast.makeText(this, "Tambah Alamat Baru", Toast.LENGTH_SHORT).show()
            // Buka AddressFormActivity untuk tambah
            startActivity(Intent(this, AddressFormActivity::class.java))
        }
    }
}