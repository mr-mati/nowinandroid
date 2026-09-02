package com.google.samples.apps.nowinandroid.feature.news.api.navigation

import androidx.navigation3.runtime.NavKey
import com.google.samples.apps.nowinandroid.core.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
object NewsNavKey : NavKey

@Serializable
data class NewsDetailNavKey(val id: String) : NavKey

fun Navigator.navigateToNewsDetail(newsId: String) {
    navigate(NewsDetailNavKey(newsId))
}
