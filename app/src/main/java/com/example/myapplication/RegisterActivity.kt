package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Back
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Terms & Conditions
        binding.tvTerms.setOnClickListener {
            Toast.makeText(this, "📜 Buka Syarat & Ketentuan", Toast.LENGTH_SHORT).show()
        }

        // Privacy Policy
        binding.tvPrivacy.setOnClickListener {
            Toast.makeText(this, "🔒 Buka Kebijakan Privasi", Toast.LENGTH_SHORT).show()
        }

        // Register
        binding.btnRegister.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)

            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val isTermsChecked = binding.cbTerms.isChecked

            if (validateForm(fullName, email, password, confirmPassword, isTermsChecked)) {
                // Simulasi registrasi sukses
                Toast.makeText(
                    this,
                    "✅ Pendaftaran berhasil! Silakan masuk.",
                    Toast.LENGTH_LONG
                ).show()

                // Navigasi ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        // Social Login
        binding.btnGoogle.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)
            Toast.makeText(this, "🔵 Daftar dengan Google", Toast.LENGTH_SHORT).show()
        }

        binding.btnApple.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)
            Toast.makeText(this, "⚪ Daftar dengan Apple", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebook.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)
            Toast.makeText(this, "🔵 Daftar dengan Facebook", Toast.LENGTH_SHORT).show()
        }

        // Login link
        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateForm(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        isTermsChecked: Boolean
    ): Boolean {
        var isValid = true

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Nama lengkap harus diisi"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email harus diisi"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format email tidak valid"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Kata sandi harus diisi"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "Kata sandi minimal 6 karakter"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Konfirmasi kata sandi harus diisi"
            isValid = false
        } else if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Kata sandi tidak cocok"
            isValid = false
        }

        if (!isTermsChecked) {
            Toast.makeText(this, "Harap setujui Syarat & Ketentuan terlebih dahulu", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun animateView(view: android.view.View) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        }.start()
    }
}