package com.google.samples.apps.nowinandroid.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.samples.apps.nowinandroid.core.database.model.ExternalNewsResourceFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExternalNewsResourceFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ExternalNewsResourceFtsEntity>)

    @Query("SELECT link FROM externalNewsResourcesFts WHERE externalNewsResourcesFts MATCH :query")
    fun searchAllExternalNewsResources(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM externalNewsResourcesFts")
    fun getCount(): Flow<Int>
}
