plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.0.20"
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.TIUMusic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.TIUMusic"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemuicontroller.v0270)


    // YoutubeLib

    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.fragment.compose)


    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.1")
    // https://mvnrepository.com/artifact/org.brotli/dec
    implementation(libs.brotli.dec)
    implementation(libs.google.gson)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.encoding)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.xml)
    implementation(libs.ktor.serialization.kotlinx.protobuf)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.io)
    implementation(libs.ktor.utils)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    //Serialization
    implementation(libs.kotlinx.serialization.json)

    //SLF4J
    implementation (libs.logback.classic)
}

kapt {
    correctErrorTypes = true
}

