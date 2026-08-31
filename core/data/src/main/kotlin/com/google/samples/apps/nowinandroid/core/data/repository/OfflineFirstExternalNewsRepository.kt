package com.google.samples.apps.nowinandroid.core.data.repository

import com.google.samples.apps.nowinandroid.core.database.dao.ExternalNewsResourceDao
import com.google.samples.apps.nowinandroid.core.database.model.ExternalNewsResourceEntity
import com.google.samples.apps.nowinandroid.core.database.model.asExternalModel
import com.google.samples.apps.nowinandroid.core.model.data.ExternalNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.UserExternalNewsResource
import com.google.samples.apps.nowinandroid.core.model.data.mapToUserExternalNewsResources
import com.google.samples.apps.nowinandroid.core.network.ExternalNewsNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class OfflineFirstExternalNewsRepository @Inject constructor(
    private val externalNewsResourceDao: ExternalNewsResourceDao,
    private val userDataRepository: UserDataRepository,
    private val network: ExternalNewsNetworkDataSource,
) : ExternalNewsRepository {

    override fun getNewsFeed(): Flow<List<UserExternalNewsResource>> =
        externalNewsResourceDao.getExternalNewsResources()
            .combine(userDataRepository.userData) { news, userData ->
                news.map(ExternalNewsResourceEntity::asExternalModel).mapToUserExternalNewsResources(userData)
            }

    override fun observeAllBookmarked(): Flow<List<UserExternalNewsResource>> =
        userDataRepository.userData.map { it.bookmarkedExternalNewsResources }.distinctUntilChanged()
            .flatMapLatest { bookmarkedLinks ->
                if (bookmarkedLinks.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    externalNewsResourceDao.getExternalNewsResources(bookmarkedLinks)
                        .combine(userDataRepository.userData) { news, userData ->
                            news.map(ExternalNewsResourceEntity::asExternalModel)
                                .mapToUserExternalNewsResources(userData)
                        }
                }
            }

    override suspend fun refreshNews(): Result<Unit> = runCatching {
        val networkNews = network.getNewsFeed()
        if (networkNews.isNotEmpty()) {
            externalNewsResourceDao.upsertExternalNewsResources(
                networkNews.map {
                    ExternalNewsResourceEntity(
                        title = it.title,
                        link = it.link,
                        imageUrl = it.og,
                        source = it.source,
                        sourceIconUrl = it.source_icon,
                        category = it.category,
                    )
                },
            )
        }
    }
}