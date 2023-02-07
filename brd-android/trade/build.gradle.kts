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

redacted {
    replacementString.set("***")
}

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
    implementation(project(":brd-android:registration"))
    implementation(project(":brd-android:rockwallet-common"))

    implementation(Libs.Androidx.AppCompat)
    implementation(Libs.Androidx.Biometric)
    implementation(Libs.Androidx.Browser)
    implementation(Libs.Androidx.CoreKtx)
    implementation(Libs.Androidx.LifecycleLiveDataKtx)
    implementation(Libs.Androidx.LifecycleViewModelKtx)

    implementation(Libs.Material.Core)

    implementation(Libs.Networking.Retrofit)
    implementation(Libs.Networking.RetrofitMoshiConverter)

    implementation(Libs.Networking.Moshi)
    kapt(Libs.Networking.MoshiCodegen)

    implementation(Libs.Glide.Core)
    kapt(Libs.Glide.Compiler)

    testImplementation(Libs.JUnit.Core)
    testImplementation(Libs.Mockito.Core)
}
