package com.dicoding.storyapp.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.databinding.ItemStoryBinding

class StoryAdapter(
    private val onItemClick: (ListStoryItem, ActivityOptionsCompat) -> Unit
) : PagingDataAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)

            holder.itemView.setOnClickListener {
                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as androidx.appcompat.app.AppCompatActivity,
                    androidx.core.util.Pair(holder.binding.ivItemPhoto, "story_photo"),
                    androidx.core.util.Pair(holder.binding.tvItemName, "story_name"),
                    androidx.core.util.Pair(holder.binding.tvItemDescriptionPreview, "story_description")
                )
                onItemClick(story, optionsCompat)
            }
        }
    }

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.tvItemName.text = story.name
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemDescriptionPreview.text = story.description
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}