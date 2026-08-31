package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.google.samples.apps.nowinandroid.core.database.model.ExternalNewsResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExternalNewsResourceDao {
    @Query(value = "SELECT * FROM external_news_resources")
    fun getExternalNewsResources(): Flow<List<ExternalNewsResourceEntity>>

    @Query(
        value = """
        SELECT * FROM external_news_resources
        WHERE link IN (:ids)
    """,
    )
    fun getExternalNewsResources(ids: Set<String>): Flow<List<ExternalNewsResourceEntity>>

    @Upsert
    suspend fun upsertExternalNewsResources(entities: List<ExternalNewsResourceEntity>)

    @Query(value = "DELETE FROM external_news_resources")
    suspend fun clearAll()
}
