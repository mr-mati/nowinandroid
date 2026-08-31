package com.google.samples.apps.nowinandroid.core.model.data

data class ExternalNewsResource(
    val title: String,
    val link: String,
    val imageUrl: String?,
    val source: String,
    val sourceIconUrl: String?,
    val category: String?,
)