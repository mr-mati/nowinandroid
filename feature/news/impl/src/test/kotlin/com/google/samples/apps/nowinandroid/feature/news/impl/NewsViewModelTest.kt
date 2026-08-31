package com.google.samples.apps.nowinandroid.feature.news.impl

import app.cash.turbine.test
import com.google.samples.apps.nowinandroid.core.data.repository.ExternalNewsRepository
import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource
import com.google.samples.apps.nowinandroid.core.testing.util.MainDispatcherRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val newsRepository = TestExternalNewsRepository()
    private lateinit var viewModel: NewsViewModel

    @Before
    fun setup() {
        viewModel = NewsViewModel(newsRepository)
    }

    @Test
    fun stateIsInitiallyLoading() = runTest {
        assertEquals(NewsUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun stateIsSuccessAfterRefresh() = runTest {
        viewModel.uiState.test {
            assertEquals(NewsUiState.Loading, awaitItem())

            newsRepository.sendNews(sampleNews)
            viewModel.refresh()

            val successState = awaitItem() as NewsUiState.Success
            assertEquals(sampleNews, successState.news)
        }
    }

    @Test
    fun stateIsEmptyWhenNoNews() = runTest {
        viewModel.uiState.test {
            assertEquals(NewsUiState.Loading, awaitItem())

            newsRepository.sendNews(emptyList())
            val emptyState = awaitItem()
            assertEquals(NewsUiState.Empty, emptyState)
        }
    }

    @Test
    fun stateIsErrorWhenRefreshFailsAndNoData() = runTest {
        viewModel.uiState.test {
            assertEquals(NewsUiState.Loading, awaitItem())

            newsRepository.setReturnError(true)
            newsRepository.sendNews(emptyList())
            viewModel.refresh()

            val errorState = awaitItem()
            assertEquals(NewsUiState.Error, errorState)
        }
    }
}

private class TestExternalNewsRepository : ExternalNewsRepository {
    private val newsFlow = MutableSharedFlow<List<ExternalNewsResource>>(replay = 1)
    private var returnError = false

    fun sendNews(news: List<ExternalNewsResource>) {
        newsFlow.tryEmit(news)
    }

    fun setReturnError(value: Boolean) {
        returnError = value
    }

    override fun getNewsFeed(): Flow<List<ExternalNewsResource>> = newsFlow

    override suspend fun refreshNews(): Result<Unit> {
        return if (returnError) Result.failure(Exception("Error")) else Result.success(Unit)
    }
}

private val sampleNews = listOf(
    ExternalNewsResource(
        title = "Title 1",
        link = "link1",
        imageUrl = null,
        source = "Source 1",
        sourceIconUrl = null
    )
)
