package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        // Back
        binding.ivBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Forgot Password
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(this, "🔑 Fitur lupa kata sandi akan segera hadir", Toast.LENGTH_SHORT).show()
        }

        // Login
        binding.btnLogin.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateForm(email, password)) {
                // Simulasi login sukses
                Toast.makeText(this, "✅ Login berhasil! Selamat datang kembali!", Toast.LENGTH_LONG).show()
                
                // Navigasi ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        // Google Login
        binding.btnGoogle.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)
            Toast.makeText(this, "🔵 Login dengan Google", Toast.LENGTH_SHORT).show()
        }

        // Apple Login
        binding.btnApple.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)
            Toast.makeText(this, "⚪ Login dengan Apple", Toast.LENGTH_SHORT).show()
        }

        // Facebook Login
        binding.btnFacebook.setOnClickListener { view ->
            view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
            animateView(view)
            Toast.makeText(this, "🔵 Login dengan Facebook", Toast.LENGTH_SHORT).show()
        }

        // Register
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true

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

        return isValid
    }

    private fun animateView(view: android.view.View) {
        view.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).withEndAction {
            view.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
        }.start()
    }
}