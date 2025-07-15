package com.dicoding.storyapp

import com.dicoding.storyapp.data.remote.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryList(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "story-$i",
                photoUrl = "http://photo.url/story-$i",
                createdAt = "2024-07-12T10:00:00Z",
                name = "Name $i",
                description = "Description $i",
                lat = i.toDouble() * 0.1,
                lon = i.toDouble() * 0.2
            )
            items.add(story)
        }
        return items
    }
}