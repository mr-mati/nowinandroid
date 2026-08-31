package com.google.samples.apps.nowinandroid.core.network

import com.google.samples.apps.nowinandroid.core.network.model.NetworkExternalNewsResource

interface ExternalNewsNetworkDataSource {
    suspend fun getNewsFeed(): List<NetworkExternalNewsResource>
}