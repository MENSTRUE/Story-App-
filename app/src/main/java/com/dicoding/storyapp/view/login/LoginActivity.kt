package com.dicoding.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.view.signup.SignupActivity
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[AuthViewModel::class.java]

        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            var isValid = true

            binding.emailEditText.error = null
            binding.passwordEditText.error = null

            if (email.isEmpty()) {
                binding.emailEditText.error = "Email tidak boleh kosong"
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "Format email tidak valid (contoh: user@example.com)"
                isValid = false
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Password tidak boleh kosong"
                isValid = false
            } else if (password.length < 8) {
                binding.passwordEditText.error = "Password minimal 8 karakter"
                isValid = false
            }

            if (!isValid) {
                Toast.makeText(this, "Perbaiki kesalahan input sebelum login", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(email, password)
        }

        binding.root.findViewById<TextView>(R.id.registerText)?.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Login gagal: ${result.error}. Periksa email dan password Anda.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !isLoading
        binding.emailEditText.isEnabled = !isLoading
        binding.passwordEditText.isEnabled = !isLoading
        binding.emailEditTextLayout.isEnabled = !isLoading
        binding.passwordEditTextLayout.isEnabled = !isLoading
        binding.root.findViewById<View>(R.id.registerText)?.isEnabled = !isLoading
    }

    private fun playAnimation() {
        val imageView = ObjectAnimator.ofFloat(binding.imageView, View.ALPHA, 1f).setDuration(200)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(200)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(200)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val loginButton = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(200)
        val registerText = ObjectAnimator.ofFloat(binding.root.findViewById<View>(R.id.registerText), View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                imageView,
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                loginButton,
                registerText
            )
            start()
        }
    }
}