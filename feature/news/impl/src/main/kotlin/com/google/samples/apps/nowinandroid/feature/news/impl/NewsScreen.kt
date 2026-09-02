/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.feature.news.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaButton
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaOverlayLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.DraggableScrollbar
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.rememberDraggableScroller
import com.google.samples.apps.nowinandroid.core.designsystem.component.scrollbar.scrollbarState
import com.google.samples.apps.nowinandroid.core.designsystem.icon.NiaIcons
import com.google.samples.apps.nowinandroid.core.ui.ExternalNewsResourceCard
import com.google.samples.apps.nowinandroid.feature.news.api.R
import com.google.samples.apps.nowinandroid.feature.news.api.navigation.navigateToNewsDetail

@Composable
internal fun NewsRoute(
    navigator: com.google.samples.apps.nowinandroid.core.navigation.Navigator,
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState !is NewsUiState.Success) {
            viewModel.refresh()
        }
    }

    NewsScreen(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onToggleBookmark = viewModel::toggleBookmark,
        onNewsClick = { newsId ->
            // Open internal detail screen instead of external browser
            navigator.navigateToNewsDetail(newsId)
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsScreen(
    uiState: NewsUiState,
    onRefresh: () -> Unit,
    onToggleBookmark: (String, Boolean) -> Unit,
    onNewsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            NewsUiState.Loading -> {
                // Showing nothing in the list, overlay will handle it
            }

            NewsUiState.Empty -> {
                NewsEmptyState()
            }

            is NewsUiState.Success -> {
                val scrollableState = rememberLazyStaggeredGridState()
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = onRefresh,
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(300.dp),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 24.dp,
                            state = scrollableState,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            items(uiState.news, key = { it.link }) { item ->
                                ExternalNewsResourceCard(
                                    userExternalNewsResource = item,
                                    onToggleBookmark = { onToggleBookmark(item.link, item.isBookmarked) },
                                    onClick = { onNewsClick(item.link) },
                                )
                            }
                            item(span = StaggeredGridItemSpan.FullLine) {
                                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
                            }
                        }
                        val itemsAvailable = uiState.news.size
                        val scrollbarState = scrollableState.scrollbarState(
                            itemsAvailable = itemsAvailable,
                        )
                        scrollableState.DraggableScrollbar(
                            modifier = Modifier
                                .fillMaxHeight()
                                .windowInsetsPadding(WindowInsets.systemBars)
                                .padding(horizontal = 2.dp)
                                .align(Alignment.CenterEnd),
                            state = scrollbarState,
                            orientation = Orientation.Vertical,
                            onThumbMoved = scrollableState.rememberDraggableScroller(
                                itemsAvailable = itemsAvailable,
                            ),
                        )
                    }
                }
            }

            NewsUiState.Error -> {
                NewsErrorState(onRetry = onRefresh)
            }
        }

        AnimatedVisibility(
            visible = uiState is NewsUiState.Loading,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val loadingContentDescription = stringResource(id = R.string.feature_news_api_loading)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                NiaOverlayLoadingWheel(
                    contentDesc = loadingContentDescription,
                )
            }
        }
    }
}

@Composable
private fun NewsEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("news:empty"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = NiaIcons.UpcomingBorder,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.feature_news_api_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.feature_news_api_empty_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NewsErrorState(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("news:error"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = NiaIcons.UpcomingBorder,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.feature_news_api_error_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.feature_news_api_error_description),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        NiaButton(onClick = onRetry) {
            Text(
                text = stringResource(R.string.feature_news_api_retry_button),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
