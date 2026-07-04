package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.myapplication.databinding.ActivityAddressFormBinding
import com.example.myapplication.model.Address

class AddressFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressFormBinding
    private var isEditMode = false
    private var addressId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cek mode (edit atau tambah)
        val address = intent.getSerializableExtra("address") as? Address
        if (address != null) {
            isEditMode = true
            addressId = address.id
            binding.tvTitle.text = "Edit Alamat"
            populateForm(address)
        } else {
            isEditMode = false
            binding.tvTitle.text = "Tambah Alamat"
            binding.btnDelete.visibility = android.view.View.GONE
        }

        setupSpinners()
        setupListeners()
    }

    private fun populateForm(address: Address) {
        binding.etReceiverName.setText(address.name)
        binding.etReceiverPhone.setText(address.phone)
        binding.etAddressDetail.setText(address.address)
        binding.switchDefault.isChecked = address.isDefault
        // Untuk spinner, set selection sesuai data
        // ...
    }

    private fun setupSpinners() {
        // Provinsi
        val provinces = arrayOf("DKI Jakarta", "Jawa Barat", "Jawa Timur", "Jawa Tengah", "Bali")
        val provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, provinces)
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerProvince.adapter = provinceAdapter

        // Kota
        val cities = arrayOf("Jakarta Pusat", "Jakarta Selatan", "Jakarta Utara", "Jakarta Barat", "Jakarta Timur")
        val cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, cities)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = cityAdapter

        // Kecamatan
        val districts = arrayOf("Menteng", "Tanah Abang", "Senayan", "Kebayoran Baru", "Gambir")
        val districtAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, districts)
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDistrict.adapter = districtAdapter
    }

    private fun setupListeners() {
        // Back
        binding.ivBack.setOnClickListener { finish() }

        // Delete
        binding.btnDelete.setOnClickListener {
            if (isEditMode) {
                Toast.makeText(this, "🗑️ Alamat berhasil dihapus", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // Save
        binding.btnSave.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            }

            if (validateForm()) {
                val name = binding.etReceiverName.text.toString()
                val phone = binding.etReceiverPhone.text.toString()
                val addressDetail = binding.etAddressDetail.text.toString()
                val province = binding.spinnerProvince.selectedItem.toString()
                val city = binding.spinnerCity.selectedItem.toString()
                val district = binding.spinnerDistrict.selectedItem.toString()
                val postalCode = binding.etPostalCode.text.toString()
                val isDefault = binding.switchDefault.isChecked

                val fullAddress = "$addressDetail\n$city, $province $postalCode"

                // Simpan ke database atau kembali ke AddressActivity
                if (isEditMode) {
                    Toast.makeText(this, "✅ Alamat berhasil diperbarui", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "✅ Alamat berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etReceiverName.text.isNullOrBlank()) {
            binding.etReceiverName.error = "Nama penerima harus diisi"
            isValid = false
        }

        if (binding.etReceiverPhone.text.isNullOrBlank()) {
            binding.etReceiverPhone.error = "Nomor telepon harus diisi"
            isValid = false
        }

        if (binding.etAddressDetail.text.isNullOrBlank()) {
            binding.etAddressDetail.error = "Alamat harus diisi"
            isValid = false
        }

        if (binding.etPostalCode.text.isNullOrBlank()) {
            binding.etPostalCode.error = "Kode pos harus diisi"
            isValid = false
        }

        return isValid
    }
}