import brd.BrdRelease
import brd.Libs

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

apply(from = rootProject.file("gradle/flavors.gradle"))

android {
    compileSdkVersion(BrdRelease.ANDROID_COMPILE_SDK)
    buildToolsVersion(BrdRelease.ANDROID_BUILD_TOOLS)
    defaultConfig {
        minSdkVersion(BrdRelease.ANDROID_MINIMUM_SDK)
        buildConfigField("int", "VERSION_CODE", "${BrdRelease.versionCode}")
    }
    lintOptions {
        isAbortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":brd-android:app-core"))
    implementation(project(":brd-android:rockwallet-common"))
    implementation(project(":brd-android:registration"))

    implementation(Libs.AndroidxCamera.Core)
    implementation(Libs.AndroidxCamera.Camera2)
    implementation(Libs.AndroidxCamera.Lifecycle)
    implementation(Libs.AndroidxCamera.View)
    implementation(Libs.AndroidxCamera.Extensions)

    implementation(Libs.Picasso.Core)

    // Kodein DI
    implementation(Libs.Kodein.CoreErasedJvm)
    implementation(Libs.Kodein.FrameworkAndroidX)

    implementation(Libs.Glide.Core)
    kapt(Libs.Glide.Compiler)

    implementation(Libs.Networking.Retrofit)
    implementation(Libs.Networking.RetrofitMoshiConverter)

    implementation(Libs.Networking.Moshi)
    kapt(Libs.Networking.MoshiCodegen)

    testImplementation(Libs.JUnit.Core)
    testImplementation(Libs.Mockito.Core)
}