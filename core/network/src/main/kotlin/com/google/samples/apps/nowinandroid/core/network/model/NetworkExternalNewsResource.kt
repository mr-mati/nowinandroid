package com.google.samples.apps.nowinandroid.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkExternalNewsResource(
    val title: String,
    val link: String,
    val og: String? = null,
    val source: String,
    val source_icon: String? = null,
    val category: String? = null,
)