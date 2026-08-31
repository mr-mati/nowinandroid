/*
 * Copyright 2024 The Android Open Source Project
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

package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.ExternalNewsResourceEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [ExternalNewsResourceEntity] access
 */
@Dao
interface ExternalNewsResourceDao {
    @Query(value = "SELECT * FROM external_news_resources")
    fun getExternalNewsResources(): Flow<List<ExternalNewsResourceEntity>>

    @Upsert
    suspend fun upsertExternalNewsResources(entities: List<ExternalNewsResourceEntity>)

    @Query(value = "DELETE FROM external_news_resources")
    suspend fun clearAll()
}
