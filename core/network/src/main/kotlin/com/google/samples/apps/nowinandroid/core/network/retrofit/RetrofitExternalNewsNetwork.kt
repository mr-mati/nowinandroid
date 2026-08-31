package com.google.samples.apps.nowinandroid.core.network.retrofit

import androidx.tracing.trace
import com.google.samples.apps.nowinandroid.core.network.ExternalNewsNetworkDataSource
import com.google.samples.apps.nowinandroid.core.network.model.NetworkExternalNewsResource
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton

private interface RetrofitExternalNewsNetworkApi {
    @GET(value = "cors/news-feed")
    suspend fun getNewsFeed(): Map<String, List<NetworkExternalNewsResource>>
}

private const val OK_SURF_BASE_URL = "https://ok.surf/api/v1/"

@Singleton
internal class RetrofitExternalNewsNetwork @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: dagger.Lazy<Call.Factory>,
) : ExternalNewsNetworkDataSource {

    private val networkApi = trace("RetrofitExternalNewsNetwork") {
        Retrofit.Builder()
            .baseUrl(OK_SURF_BASE_URL)
            .callFactory { okhttpCallFactory.get().newCall(it) }
            .addConverterFactory(
                networkJson.asConverterFactory("application/json".toMediaType()),
            )
            .build()
            .create(RetrofitExternalNewsNetworkApi::class.java)
    }

    override suspend fun getNewsFeed(): List<NetworkExternalNewsResource> =
        networkApi.getNewsFeed().flatMap { (category, newsList) ->
            newsList.map { it.copy(category = category) }
        }
}