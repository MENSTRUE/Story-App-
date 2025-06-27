package com.dicoding.storyapp.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.view.addstory.AddStoryActivity
import com.dicoding.storyapp.view.liststory.ListStoryFragment
import com.dicoding.storyapp.view.welcome.WelcomeActivity
import com.dicoding.storyapp.viewmodel.AuthViewModel
import com.dicoding.storyapp.viewmodel.MainViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var toggle: ActionBarDrawerToggle
    private var userToken: String = ""

    private val addStoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadListStoryFragment()
            binding.navView.setCheckedItem(R.id.nav_list_story)
            Toast.makeText(this, "Cerita berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()

        val factory = ViewModelFactory.getInstance(this)
        authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupDrawer()

        authViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                userToken = user.token
                loadListStoryFragment()
                observeStories()
            }
        }

        setupAction()
    }

    override fun onResume() {
        super.onResume()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is ListStoryFragment) {
            binding.navView.setCheckedItem(R.id.nav_list_story)
            if (userToken.isNotEmpty()) {
                mainViewModel.getStories(userToken)
            }
        }
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupDrawer() {
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.app_name, R.string.app_name)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_list_story -> {
                    loadListStoryFragment()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    menuItem.isChecked = true
                }
                R.id.nav_add_story -> {
                    val intent = Intent(this, AddStoryActivity::class.java)
                    addStoryLauncher.launch(intent)
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_logout -> {
                    authViewModel.logout()
                    val intent = Intent(this, WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_language_settings -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
            }
            true
        }
        binding.navView.setCheckedItem(R.id.nav_list_story)
    }

    private fun loadListStoryFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment !is ListStoryFragment) {
            val fragment = ListStoryFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    fun getUserToken(): String {
        return userToken
    }

    private fun setupAction() {
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            addStoryLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {

    }

    private fun observeStories() {
        if (userToken.isNotEmpty()) {
            mainViewModel.getStories(userToken)
        }


        mainViewModel.toastText.observe(this) { toastText ->
            toastText.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}