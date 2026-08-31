package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.UserExternalNewsResource
import kotlinx.coroutines.flow.Flow

interface ExternalNewsRepository {
    fun getNewsFeed(): Flow<List<UserExternalNewsResource>>
    fun observeAllBookmarked(): Flow<List<UserExternalNewsResource>>
    suspend fun refreshNews(): Result<Unit>
}
