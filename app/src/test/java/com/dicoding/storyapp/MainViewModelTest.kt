package com.dicoding.storyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.data.remote.response.ListStoryItem
import com.dicoding.storyapp.view.main.StoryAdapter
import com.dicoding.storyapp.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private val dummyToken = "dummy_token"

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryList()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStories)
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        // Memastikan userRepository mengembalikan data yang diharapkan
        Mockito.`when`(userRepository.getStoriesPaging(dummyToken)).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(userRepository)
        val actualStories: PagingData<ListStoryItem> = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        // Memastikan data tidak null
        Assert.assertNotNull(differ.snapshot())
        // Memastikan jumlah data sesuai dengan yang diharapkan
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        // Memastikan data pertama yang dikembalikan sesuai
        Assert.assertEquals(dummyStories[0].id, differ.snapshot().items[0].id)
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
        expectedStories.value = data

        Mockito.`when`(userRepository.getStoriesPaging(dummyToken)).thenReturn(expectedStories)

        val mainViewModel = MainViewModel(userRepository)
        val actualStories: PagingData<ListStoryItem> = mainViewModel.getStories(dummyToken).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStories)

        // Memastikan jumlah data yang dikembalikan nol
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

// Utilitas untuk menghasilkan snapshot PagingData untuk pengujian
// Kita tidak perlu mengimplementasikan PagingSource lengkap di sini jika hanya digunakan untuk snapshot.
class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<Int, ListStoryItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), null, null)
    }
}


// Callback untuk AsyncPagingDataDiffer
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}