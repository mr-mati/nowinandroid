/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.core.data.test.repository

import com.google.samples.apps.nowinandroid.core.data.repository.ExternalNewsRepository
import com.google.samples.apps.nowinandroid.core.data.repository.UserDataRepository
import com.google.samples.apps.nowinandroid.core.model.data.UserExternalNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.mapToUserExternalNewsResources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FakeExternalNewsRepository @Inject constructor(
    private val userDataRepository: UserDataRepository,
) : ExternalNewsRepository {

    private val newsResources = MutableStateFlow(emptyList<com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource>())

    override fun getNewsFeed(): Flow<List<UserExternalNewsResource>> =
        newsResources.combine(userDataRepository.userData) { news, userData ->
            news.mapToUserExternalNewsResources(userData)
        }

    override fun observeAllBookmarked(): Flow<List<UserExternalNewsResource>> =
        getNewsFeed().map { news -> news.filter { it.isBookmarked } }

    override suspend fun refreshNews(): Result<Unit> = Result.success(Unit)
}
