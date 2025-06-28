package com.dicoding.storyapp.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.databinding.ActivityAddStoryBinding
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.viewmodel.AddStoryViewModel
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory
import com.dicoding.utils.createCustomTempFile
import com.dicoding.utils.reduceFileImage
import com.dicoding.utils.uriToFile
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var authViewModel: AuthViewModel

    private var currentImageUri: Uri? = null

    private var userToken: String = ""

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Izin diberikan", Toast.LENGTH_SHORT).show()
            startCamera()
        } else {
            Toast.makeText(this, "Izin ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val factory = ViewModelFactory.getInstance(this)
        addStoryViewModel = ViewModelProvider(this, factory)[AddStoryViewModel::class.java]
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        authViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                userToken = user.token
            } else {
                Toast.makeText(this, "Sesi Anda telah berakhir, silakan login kembali.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }

        setupAction()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Story"
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadStory() }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            // PERBAIKAN DI SINI: Tangani jika pengguna membatalkan pengambilan gambar kamera
            currentImageUri = null // Set URI menjadi null jika dibatalkan
            binding.ivStoryPreview.setImageDrawable(null) // Bersihkan gambar preview
            Toast.makeText(this, "Pengambilan gambar dibatalkan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val photoFile: File = createCustomTempFile(application)
        val photoUri: Uri = FileProvider.getUriForFile(
            this,
            "${applicationContext.packageName}.fileprovider",
            photoFile
        )
        currentImageUri = photoUri
        launcherIntentCamera.launch(photoUri)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            // PERBAIKAN DI SINI: Tangani jika pengguna membatalkan pemilihan gambar galeri
            currentImageUri = null // Set URI menjadi null jika dibatalkan
            binding.ivStoryPreview.setImageDrawable(null) // Bersihkan gambar preview
            Toast.makeText(this, "Tidak ada media yang dipilih", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        launcherIntentGallery.launch("image/*")
    }

    private fun showImage() {
        val imageUriToDisplay = currentImageUri
        imageUriToDisplay?.let {
            binding.ivStoryPreview.setImageURI(it)
        }
    }

    private fun uploadStory() {
        val imageUriToUpload = currentImageUri
        // PERBAIKAN DI SINI: Pastikan ada gambar yang dipilih sebelum mencoba mengunggah
        imageUriToUpload?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            val description = binding.edAddDescription.text.toString().trim()

            var isValid = true

            binding.edAddDescription.error = null

            if (description.isEmpty()) {
                binding.edAddDescription.error = "Deskripsi tidak boleh kosong"
                isValid = false
            }

            if (!isValid) {
                Toast.makeText(this, "Perbaiki kesalahan input sebelum mengunggah", Toast.LENGTH_SHORT).show()
                return
            }

            if (userToken.isEmpty()) {
                Toast.makeText(this, "Token pengguna tidak ditemukan, silakan login ulang.", Toast.LENGTH_LONG).show()
                return
            }

            addStoryViewModel.addStory(userToken, imageFile, description)

        } ?: Toast.makeText(this, "Silakan masukkan gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        addStoryViewModel.addStoryResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    Toast.makeText(this, result.data, Toast.LENGTH_LONG).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(this, "Gagal menambahkan cerita: ${result.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
        addStoryViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
        addStoryViewModel.toastText.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnUpload.isEnabled = !isLoading
        binding.btnCamera.isEnabled = !isLoading
        binding.btnGallery.isEnabled = !isLoading
        binding.edAddDescription.isEnabled = !isLoading
    }
}