package com.google.samples.apps.nowinandroid.feature.news.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.google.samples.apps.nowinandroid.core.navigation.Navigator
import com.google.samples.apps.nowinandroid.feature.news.api.navigation.NewsNavKey
import com.google.samples.apps.nowinandroid.feature.news.impl.NewsRoute

fun EntryProviderScope<NavKey>.newsEntry(navigator: Navigator) {
    entry<NewsNavKey> {
        NewsRoute()
    }
}
