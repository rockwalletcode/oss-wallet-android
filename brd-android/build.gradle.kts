buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }

    dependencies {
        classpath(brd.Libs.Android.GradlePlugin)
        classpath(brd.Libs.Google.ServicesPlugin)
        classpath(brd.Libs.Android.SafeArgsPlugin)
        classpath(brd.Libs.Kotlin.GradlePlugin)
        classpath(brd.Libs.Firebase.DistributionPlugin)
        classpath(brd.Libs.Firebase.CrashlyticsPlugin)
        classpath(brd.Libs.Tests.JacocoPlugin)
    }
}

plugins {
    id("dev.zacsweers.redacted") version "0.8.0"
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://www.jitpack.io")
    }
}

subprojects {
    apply(plugin = "dev.zacsweers.redacted")
}