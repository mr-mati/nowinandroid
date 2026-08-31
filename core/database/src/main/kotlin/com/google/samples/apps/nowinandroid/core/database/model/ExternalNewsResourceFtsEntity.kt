package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Entity(tableName = "externalNewsResourcesFts")
@Fts4
data class ExternalNewsResourceFtsEntity(
    @ColumnInfo(name = "link")
    val link: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "source")
    val source: String,
)

fun ExternalNewsResourceEntity.asFtsEntity() = ExternalNewsResourceFtsEntity(
    link = link,
    title = title,
    source = source,
)
