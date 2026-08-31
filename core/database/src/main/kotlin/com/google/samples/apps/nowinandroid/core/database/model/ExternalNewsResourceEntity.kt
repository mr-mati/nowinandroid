package com.google.samples.apps.nowinandroid.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource

@Entity(
    tableName = "external_news_resources",
)
data class ExternalNewsResourceEntity(
    @PrimaryKey
    val link: String,
    val title: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,
    val source: String,
    @ColumnInfo(name = "source_icon_url")
    val sourceIconUrl: String?,
    val category: String?,
)

fun ExternalNewsResourceEntity.asExternalModel() = ExternalNewsResource(
    title = title,
    link = link,
    imageUrl = imageUrl,
    source = source,
    sourceIconUrl = sourceIconUrl,
    category = category,
)