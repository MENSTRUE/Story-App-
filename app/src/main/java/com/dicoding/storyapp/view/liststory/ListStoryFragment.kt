package com.dicoding.storyapp.view.liststory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.FragmentListStoryBinding
import com.dicoding.storyapp.view.addstory.AddStoryActivity
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.view.main.StoryAdapter
import com.dicoding.storyapp.viewmodel.MainViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireContext())
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        setupRecyclerView()
        setupAction()
        observeStories()

        (activity as? MainActivity)?.getUserToken()?.let { token ->
            if (token.isNotEmpty()) {
                mainViewModel.getStories(token)
            } else {
                Toast.makeText(requireContext(), "Token pengguna tidak tersedia.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStories.adapter = storyAdapter

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem, optionsCompat: androidx.core.app.ActivityOptionsCompat) {
                Toast.makeText(requireContext(), "Clicked: ${data.name}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupAction() {
        binding.fabAddStoryFragment.setOnClickListener {
            val intent = Intent(requireContext(), AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeStories() {
        mainViewModel.storyList.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    storyAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(requireContext(), "Gagal memuat cerita: ${result.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}