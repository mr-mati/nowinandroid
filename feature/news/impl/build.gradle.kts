plugins {
    alias(libs.plugins.nowinandroid.android.feature.impl)
    alias(libs.plugins.nowinandroid.android.library.compose)
}

android {
    namespace = "com.google.samples.apps.nowinandroid.feature.news.impl"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.ui)
    implementation(projects.feature.news.api)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
}