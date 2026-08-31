package com.google.samples.apps.nowinandroid.feature.news.impl

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource
import org.junit.Rule
import org.junit.Test

class NewsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicator_isDisplayed() {
        composeTestRule.setContent {
            NewsScreen(
                uiState = NewsUiState.Loading,
                onRefresh = {},
                onNewsClick = {}
            )
        }

        composeTestRule.onNodeWithTag("news:loading").assertIsDisplayed()
    }

    @Test
    fun emptyState_isDisplayed() {
        composeTestRule.setContent {
            NewsScreen(
                uiState = NewsUiState.Empty,
                onRefresh = {},
                onNewsClick = {}
            )
        }

        composeTestRule.onNodeWithTag("news:empty").assertIsDisplayed()
        composeTestRule.onNodeWithText("No news yet").assertIsDisplayed()
    }

    @Test
    fun errorState_isDisplayed() {
        composeTestRule.setContent {
            NewsScreen(
                uiState = NewsUiState.Error,
                onRefresh = {},
                onNewsClick = {}
            )
        }

        composeTestRule.onNodeWithTag("news:error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Oops! Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun successState_isDisplayed() {
        val news = listOf(
            ExternalNewsResource(
                title = "Title 1",
                link = "link1",
                imageUrl = null,
                source = "Source 1",
                sourceIconUrl = null
            )
        )
        composeTestRule.setContent {
            NewsScreen(
                uiState = NewsUiState.Success(news, false),
                onRefresh = {},
                onNewsClick = {}
            )
        }

        composeTestRule.onNodeWithText("Title 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Source 1").assertIsDisplayed()
    }
}
