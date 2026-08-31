package com.google.samples.apps.nowinandroid.core.model.data

data class UserExternalNewsResource(
    val title: String,
    val link: String,
    val imageUrl: String?,
    val source: String,
    val sourceIconUrl: String?,
    val category: String?,
    val isBookmarked: Boolean,
)

fun List<ExternalNewsResource>.mapToUserExternalNewsResources(userData: UserData): List<UserExternalNewsResource> =
    map { item ->
        UserExternalNewsResource(
            title = item.title,
            link = item.link,
            imageUrl = item.imageUrl,
            source = item.source,
            sourceIconUrl = item.sourceIconUrl,
            category = item.category,
            isBookmarked = item.link in userData.bookmarkedExternalNewsResources,
        )
    }
