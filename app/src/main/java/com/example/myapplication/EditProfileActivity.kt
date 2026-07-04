package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivAvatar.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupListeners()
    }

    private fun setupSpinner() {
        val genders = arrayOf("Laki-laki", "Perempuan")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter
        // Set default ke Perempuan (index 1)
        binding.spinnerGender.setSelection(1)
    }

    private fun setupListeners() {
        // Back
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Change Photo
        binding.tvChangePhoto.setOnClickListener {
            openImagePicker()
        }

        // Avatar click (alternative)
        binding.ivAvatar.setOnClickListener {
            openImagePicker()
        }

        // Save
        binding.btnSave.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
                view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            }

            if (validateForm()) {
                val name = binding.etFullName.text.toString()
                val phone = binding.etPhone.text.toString()
                val gender = binding.spinnerGender.selectedItem.toString()
                val birthDate = binding.etBirthDate.text.toString()

                // Simpan ke database/SharedPreferences
                // ...

                Toast.makeText(
                    this,
                    "✅ Profil berhasil diperbarui!",
                    Toast.LENGTH_LONG
                ).show()

                // Kembali ke ProfileFragment
                finish()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.etFullName.text.isNullOrBlank()) {
            binding.etFullName.error = "Nama lengkap harus diisi"
            isValid = false
        }

        if (binding.etPhone.text.isNullOrBlank()) {
            binding.etPhone.error = "Nomor telepon harus diisi"
            isValid = false
        }

        return isValid
    }
}