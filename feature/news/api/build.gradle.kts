plugins {
    alias(libs.plugins.nowinandroid.android.feature.api)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.feature.news.api"
}

dependencies {
    api(projects.core.navigation)
}