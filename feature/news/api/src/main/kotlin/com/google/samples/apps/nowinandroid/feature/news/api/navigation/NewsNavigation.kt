package com.google.samples.apps.nowinandroid.feature.news.api.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.samples.apps.nowinandroid.core.navigation.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object NewsNavKey : NavKey

fun NavController.navigateToNews(navOptions: NavOptions? = null) {
    this.navigate(NewsNavKey, navOptions)
}