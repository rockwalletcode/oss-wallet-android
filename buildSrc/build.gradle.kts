repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.android.tools.build:gradle:4.1.3")
    implementation("org.json:json:20201115")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

