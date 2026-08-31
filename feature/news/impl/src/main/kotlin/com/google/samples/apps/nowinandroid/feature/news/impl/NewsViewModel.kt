package com.google.samples.apps.nowinandroid.feature.news.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.ExternalNewsRepository
import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: ExternalNewsRepository,
) : ViewModel() {

    val uiState: StateFlow<NewsUiState> = newsRepository.getNewsFeed()
        .map { news ->
            if (news.isEmpty()) NewsUiState.Empty else NewsUiState.Success(news)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsUiState.Loading,
        )

    fun refresh() {
        viewModelScope.launch {
            newsRepository.refreshNews()
        }
    }
}

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(val news: List<ExternalNewsResource>) : NewsUiState
    data object Error : NewsUiState
    data object Empty : NewsUiState
}