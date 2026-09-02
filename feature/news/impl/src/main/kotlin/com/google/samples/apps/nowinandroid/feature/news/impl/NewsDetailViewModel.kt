package com.google.samples.apps.nowinandroid.feature.news.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.nowinandroid.core.data.repository.ExternalNewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.NewsResourceQuery
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserNewsResourceRepository
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@HiltViewModel(assistedFactory = NewsDetailViewModel.Factory::class)
class NewsDetailViewModel @AssistedInject constructor(
    private val userDataRepository: UserDataRepository,
    private val userNewsResourceRepository: UserNewsResourceRepository,
    private val externalNewsRepository: ExternalNewsRepository,
    @Assisted val newsId: String,
) : ViewModel() {

    val uiState: StateFlow<NewsDetailUiState> = combine(
        userNewsResourceRepository.observeAll(
            NewsResourceQuery(filterNewsIds = setOf(newsId)),
        ),
        externalNewsRepository.getNewsFeed(),
    ) { regularNews, externalNews ->
        val news = regularNews.firstOrNull()
        if (news != null) {
            NewsDetailUiState.Success(
                id = news.id,
                title = news.title,
                content = news.content,
                url = news.url,
                headerImageUrl = news.headerImageUrl,
                publishDate = news.publishDate,
                type = news.type,
                followableTopics = news.followableTopics,
                isSaved = news.isSaved
            )
        } else {
            val external = externalNews.find { it.link == newsId }
            if (external != null) {
                NewsDetailUiState.Success(
                    id = external.link,
                    title = external.title,
                    content = "",
                    url = external.link,
                    headerImageUrl = external.imageUrl,
                    publishDate = Clock.System.now(),
                    type = "Video",
                    followableTopics = emptyList(),
                    isSaved = external.isBookmarked
                )
            } else {
                NewsDetailUiState.Error
            }
        }
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = NewsDetailUiState.Loading,
        )

    fun toggleBookmark(isBookmarked: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceBookmarked(newsId, !isBookmarked)
        }
    }

    fun setNewsResourceViewed(viewed: Boolean) {
        viewModelScope.launch {
            userDataRepository.setNewsResourceViewed(newsId, viewed)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            newsId: String,
        ): NewsDetailViewModel
    }
}

sealed interface NewsDetailUiState {
    data class Success(
        val id: String,
        val title: String,
        val content: String,
        val url: String,
        val headerImageUrl: String?,
        val publishDate: Instant,
        val type: String,
        val followableTopics: List<FollowableTopic>,
        val isSaved: Boolean,
    ) : NewsDetailUiState
    data object Error : NewsDetailUiState
    data object Loading : NewsDetailUiState
}
