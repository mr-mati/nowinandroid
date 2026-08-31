package com.google.samples.apps.nowinandroid.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.samples.apps.nowinandroid.core.database.NiaDatabase
import com.google.samples.apps.nowinandroid.core.database.model.ExternalNewsResourceEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ExternalNewsResourceDaoTest {

    private lateinit var db: NiaDatabase
    private lateinit var dao: ExternalNewsResourceDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context,
            NiaDatabase::class.java,
        ).build()
        dao = db.externalNewsResourceDao()
    }

    @After
    fun teardown() = db.close()

    @Test
    fun upsertAndGetExternalNewsResources() = runTest {
        val entities = listOf(
            ExternalNewsResourceEntity(
                link = "https://example.com/1",
                title = "Title 1",
                imageUrl = "https://example.com/image1.png",
                source = "Source 1",
                sourceIconUrl = "https://example.com/icon1.png",
                category = "Category 1",
            ),
            ExternalNewsResourceEntity(
                link = "https://example.com/2",
                title = "Title 2",
                imageUrl = "https://example.com/image2.png",
                source = "Source 2",
                sourceIconUrl = "https://example.com/icon2.png",
                category = null,
            ),
        )

        dao.upsertExternalNewsResources(entities)

        val savedEntities = dao.getExternalNewsResources().first()

        assertEquals(entities.toSet(), savedEntities.toSet())
    }
}
