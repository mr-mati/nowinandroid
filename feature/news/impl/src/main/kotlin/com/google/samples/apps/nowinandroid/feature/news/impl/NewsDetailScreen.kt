package com.google.samples.apps.nowinandroid.feature.news.impl

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.ui.BookmarkButton
import com.google.samples.apps.nowinandroid.core.ui.NewsResourceHeaderImage
import com.google.samples.apps.nowinandroid.core.ui.NewsResourceMetaData
import com.google.samples.apps.nowinandroid.core.ui.NewsResourceTopics
import com.google.samples.apps.nowinandroid.core.ui.launchCustomChromeTab
import com.google.samples.apps.nowinandroid.feature.news.api.R
import com.google.samples.apps.nowinandroid.core.ui.R as uiR

@Composable
internal fun NewsDetailRoute(
    newsId: String,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = hiltViewModel<NewsDetailViewModel, NewsDetailViewModel.Factory>(
        key = newsId,
    ) { factory ->
        factory.create(newsId)
    },
) {
    NewsDetailScreen(
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        modifier = modifier,
        viewModel = viewModel,
    )
}

@Composable
internal fun NewsDetailScreen(
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewsDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewsDetailScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onToggleBookmark = viewModel::toggleBookmark,
        onNewsResourceViewed = { viewModel.setNewsResourceViewed(true) },
        modifier = modifier,
    )
}

@Composable
internal fun NewsDetailScreenContent(
    uiState: NewsDetailUiState,
    onBackClick: () -> Unit,
    onTopicClick: (String) -> Unit,
    onToggleBookmark: (Boolean) -> Unit,
    onNewsResourceViewed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.safeDrawing))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = NiaIcons.ArrowBack,
                    contentDescription = stringResource(uiR.string.core_ui_back)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (uiState is NewsDetailUiState.Success) {
                BookmarkButton(
                    isBookmarked = uiState.isSaved,
                    onClick = { onToggleBookmark(uiState.isSaved) }
                )
            }
        }

        when (uiState) {
            NewsDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            NewsDetailUiState.Error -> {
                Text(
                    text = stringResource(R.string.feature_news_api_error_title),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }

            is NewsDetailUiState.Success -> {
                if (!uiState.headerImageUrl.isNullOrEmpty()) {
                    Box {
                        NewsResourceHeaderImage(uiState.headerImageUrl)
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = uiState.title,
                        style = MaterialTheme.typography.headlineMedium,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NewsResourceMetaData(uiState.publishDate, uiState.type)

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.content.isNotEmpty()) {
                        Text(
                            text = uiState.content,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (uiState.followableTopics.isNotEmpty()) {
                        NewsResourceTopics(
                            topics = uiState.followableTopics,
                            onTopicClick = onTopicClick
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                    }

                    val context = LocalContext.current
                    val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                    NiaButton(
                        onClick = {
                            onNewsResourceViewed()
                            launchCustomChromeTab(
                                context,
                                Uri.parse(uiState.url),
                                backgroundColor
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(uiR.string.core_ui_card_tap_action))
                    }
                }
            }
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
    }
}
}
