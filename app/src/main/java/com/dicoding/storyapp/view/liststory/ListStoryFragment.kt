package com.dicoding.storyapp.view.liststory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.databinding.FragmentListStoryBinding
import com.dicoding.storyapp.view.detail.DetailStoryActivity
import com.dicoding.storyapp.view.main.StoryAdapter
import com.dicoding.storyapp.view.main.loading.LoadingStateAdapter
import com.dicoding.storyapp.viewmodel.MainViewModel
import com.dicoding.storyapp.viewmodel.ViewModelFactory

class ListStoryFragment : Fragment() {

    private var _binding: FragmentListStoryBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

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

        setupRecyclerView()
        observeStories()
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { data, optionsCompat ->
            val intent = Intent(requireContext(), DetailStoryActivity::class.java).apply {
                putExtra(DetailStoryActivity.EXTRA_STORY_PHOTO_URL, data.photoUrl)
                putExtra(DetailStoryActivity.EXTRA_STORY_NAME, data.name)
                putExtra(DetailStoryActivity.EXTRA_STORY_DESCRIPTION, data.description)
            }
            startActivity(intent, optionsCompat.toBundle())
        }

        binding.rvStories.layoutManager = LinearLayoutManager(requireContext())

        binding.rvStories.adapter = storyAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter { storyAdapter.retry() }
        )

        storyAdapter.addLoadStateListener { loadState ->
            val isLoading = loadState.refresh is LoadState.Loading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            val isListEmpty = loadState.refresh is LoadState.NotLoading && storyAdapter.itemCount == 0
            binding.tvNoData.visibility = if (isListEmpty) View.VISIBLE else View.GONE

            val errorState = loadState.refresh as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    requireContext(),
                    "Gagal memuat cerita: ${it.error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun observeStories() {
        mainViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                mainViewModel.getStories(user.token).observe(viewLifecycleOwner) { pagingData ->
                    storyAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                }
            } else {
                Toast.makeText(requireContext(), "Sesi pengguna tidak valid.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}