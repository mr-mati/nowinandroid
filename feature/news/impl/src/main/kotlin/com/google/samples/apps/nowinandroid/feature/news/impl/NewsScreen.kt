package com.google.samples.apps.nowinandroid.feature.news.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource

@Composable
internal fun NewsRoute(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NewsScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
internal fun NewsScreen(
    uiState: NewsUiState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            NewsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            NewsUiState.Empty -> {
                Text(
                    text = "No news available",
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            is NewsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    items(uiState.news) { item ->
                        NewsItem(item = item)
                    }
                }
            }

            NewsUiState.Error -> {
                Text(
                    text = "Error loading news",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun NewsItem(
    item: ExternalNewsResource,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = item.source,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}