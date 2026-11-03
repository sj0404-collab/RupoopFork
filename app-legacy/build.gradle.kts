import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

android {
    namespace = "com.santiago43rus.rupoop.legacy"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.santiago43rus.rupoop.legacy"
        minSdk = 21
        targetSdk = 25
        versionCode = 1
        versionName = "1.0-legacy"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "PROXY_URL", "\"${localProperties.getProperty("PROXY_URL") ?: ""}\"")
    }
    lint {
        checkReleaseBuilds = false
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.core.ktx)
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.media3.exoplayer.hls)

    implementation("io.coil-kt:coil:2.7.0")
    implementation(libs.zxing.core)
}
