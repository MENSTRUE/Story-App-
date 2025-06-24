package com.dicoding.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).apply { duration = 500 }
        val nameLabel = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).apply { duration = 500 }
        val nameInput = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).apply { duration = 500 }
        val emailLabel = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).apply { duration = 500 }
        val emailInput = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).apply { duration = 500 }
        val passLabel = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).apply { duration = 500 }
        val passInput = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).apply { duration = 500 }
        val signupButton = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).apply { duration = 500 }


        // Daftar view yang akan dianimasikan ALPHA
        val alphaAnimations = listOf(
            binding.titleTextView,
            binding.nameTextView,
            binding.nameEditTextLayout,
            binding.emailTextView,
            binding.emailEditTextLayout,
            binding.passwordTextView,
            binding.passwordEditTextLayout,
            binding.signupButton
        ).map { view ->
            ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
                duration = 100
            }
        }

        AnimatorSet().apply {
            playSequentially(
                title,
                nameLabel,
                nameInput,
                emailLabel,
                emailInput,
                passLabel,
                passInput,
                signupButton
            )
            startDelay = 100
            start()
        }
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
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            AlertDialog.Builder(this).apply {
                setTitle("Yeah!")
                setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
                setPositiveButton("Lanjut") { _, _ ->
                    finish()
                }
                create()
                show()
            }
        }
    }
}