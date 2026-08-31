package com.google.samples.apps.nowinandroid.feature.news.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.ExternalNewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.UserExternalNewsResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: ExternalNewsRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    private val isRefreshing = MutableStateFlow(false)
    private val isError = MutableStateFlow(false)

    val uiState: StateFlow<NewsUiState> = combine(
        newsRepository.getNewsFeed(),
        isRefreshing,
        isError,
    ) { news, refreshing, error ->
        when {
            news.isEmpty() && refreshing -> NewsUiState.Loading
            news.isEmpty() && error -> NewsUiState.Error
            news.isEmpty() -> NewsUiState.Empty
            else -> NewsUiState.Success(news, refreshing)
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsUiState.Loading,
        )

    fun refresh() {
        viewModelScope.launch {
            isRefreshing.update { true }
            isError.update { false }
            val result = newsRepository.refreshNews()
            if (result.isFailure) {
                isError.update { true }
            }
            isRefreshing.update { false }
        }
    }

    fun toggleBookmark(link: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setExternalNewsResourceBookmarked(link, !isBookmarked)
        }
    }
}

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(
        val news: List<UserExternalNewsResource>,
        val isRefreshing: Boolean,
    ) : NewsUiState
    data object Error : NewsUiState
    data object Empty : NewsUiState
}
