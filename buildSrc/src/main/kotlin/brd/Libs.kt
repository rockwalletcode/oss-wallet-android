/**
 * BreadWallet
 *
 * Created by Drew Carlson <drew.carlson@breadwallet.com> on 10/21/20.
 * Copyright (c) 2020 breadwallet LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package brd

const val KOTLIN_VERSION = "1.5.32"
private const val KTOR_VERSION = "1.6.5"
private const val KOTLINX_ATOMICFU_VERSION = "0.16.2"
private const val KOTLINX_DATETIME_VERSION = "0.2.1"
private const val KOTLINX_SERIALIZATION_VERSION = "1.2.2"
private const val COROUTINES_VERSION = "1.5.1-native-mt"
private const val FIREBASE_APPDIST_VERSION = "2.1.1"
private const val FIREBASE_MESSAGING_VERSION = "21.0.1"
private const val FIREBASE_ANALYTICS_VERSION = "18.0.2"
private const val FIREBASE_FUNCTIONS_VERSION = "19.2.0"
private const val FIREBASE_CONFIG_VERSION = "20.0.3"
private const val CRASHLYTICS_VERSION = "17.4.1"
private const val CRASHLYTICS_PLUGIN_VERSION = "2.5.2"
private const val GUAVA_VERSION = "25.1-android"
private const val LIFECYCLE_EXT_VERSION = "2.2.0"
private const val WORK_MANAGER_VERSION = "2.7.1"
private const val SECURITY_VERSION = "1.1.0-alpha03"
private const val LEGACY_V13 = "1.0.0"
private const val CORE_VERSION = "1.3.2"
private const val APPCOMPAT_VERSION = "1.3.0-beta01"
private const val CARDVIEW_VERSION = "1.0.0"
private const val CAMERAX_VERSION = "1.0.0-rc02"
private const val CAMERAX_VIEW_VERSION = "1.0.0-alpha21"
private const val RECYCLER_VERSION = "1.1.0"
private const val CONSTRAINT_LAYOUT_VERSION = "1.1.3"
private const val GRID_LAYOUT_VERSION = "1.0.0"
private const val FASTADAPTER_VERSION = "5.3.4"
private const val CONDUCTOR_VERSION = "3.0.1"
private const val KODEIN_VERSION = "6.5.1"
private const val MOBIUS_VERSION = "1.5.3"
private const val MOBIUS_COROUTINES_VERSION = "0.1.2"
private const val ESPRESSO_VERSION = "3.3.0-rc01"
private const val ANDROIDX_TEST_VERSION = "1.3.0"
private const val JUNIT_KTX_VERSION = "1.1.2-rc01"
private const val OKHTTP_VERSION = "4.5.0"
private const val PICASSO_VERSION = "2.71828"
private const val JETTY_VERSION = "9.2.19.v20160908" // DO NOT UPGRADE
private const val LEAKCANARY_VERSION = "2.9.1"
private const val ANR_WATCHDOG_VERSION = "1.4.0"
private const val ZXING_VERSION = "3.3.3"
private const val COMMONS_IO_VERSION = "2.6"
private const val JBSDIFF_VERSION = "1.0"
private const val SLF4J_VERSION = "1.7.26"
private const val JUNIT_VERSION = "4.12"
private const val MOCKITO_VERSION = "4.6.1"
private const val KASPRESSO_VERSION = "1.1.0"
private const val KAKAO_VERSION = "2.3.2"
private const val MATERIAL_VERSION = "1.3.0"
const val JACOCO_VERSION = "0.8.6"
private const val DETEKT_VERSION = "1.0.1"
private const val COMMONS_COMPRESS_VERSION = "1.20"
private const val BIOMETRIC_VERSION = "1.2.0-alpha02"
private const val NAVIGATION_VERSION = "2.3.5"
private const val AGP_VERSION = "4.1.3"
private const val SERVICES_PLUGIN_VERSION = "4.3.4"
private const val AUTODESK_VERSION = "0.6.2"
private const val BLOCKSET_VERSION = "0.1.3"
private const val RETROFIT_VERSION = "2.9.0"
private const val MOSHI_VERSION = "1.12.0"
private const val BROWSER_VERSION = "1.4.0"
private const val GLIDE_VERSION = "4.13.0"
private const val ROBOLECTRIC_VERSION = "4.5.1"
private const val CHECKOUT_FRAMES_VERSION = "3.1.2"
private const val PLAID_VERSION = "3.7.1"
private const val LOTTIE_VERSION = "5.2.0"

object Libs {

    const val Blockset = "org.drewcarlson:blockset:$BLOCKSET_VERSION"

    object WalletKit {
        const val CoreAndroid = "com.breadwallet.core:corecrypto-android"
    }

    object Android {
        const val GradlePlugin = "com.android.tools.build:gradle:$AGP_VERSION"
        const val SafeArgsPlugin = "androidx.navigation:navigation-safe-args-gradle-plugin:$NAVIGATION_VERSION"
    }

    object Tests {
        const val JacocoPlugin = "org.jacoco:org.jacoco.core:$JACOCO_VERSION"
    }

    object Androidx {
        const val LifecycleScopeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$LIFECYCLE_EXT_VERSION"
        const val LifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:$LIFECYCLE_EXT_VERSION"
        const val LifecycleLiveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$LIFECYCLE_EXT_VERSION"
        const val LifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$LIFECYCLE_EXT_VERSION"
        const val WorkManagerKtx = "androidx.work:work-runtime-ktx:$WORK_MANAGER_VERSION"
        const val WorkManagerTesting = "androidx.work:work-testing:$WORK_MANAGER_VERSION"
        const val CoreKtx = "androidx.core:core-ktx:$CORE_VERSION"
        const val AppCompat = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
        const val CardView = "androidx.cardview:cardview:$CARDVIEW_VERSION"
        const val ConstraintLayout = "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION"
        const val GridLayout = "androidx.gridlayout:gridlayout:$GRID_LAYOUT_VERSION"
        const val RecyclerView = "androidx.recyclerview:recyclerview:$RECYCLER_VERSION"
        const val Security = "androidx.security:security-crypto:$SECURITY_VERSION"
        const val LegacyV13 = "androidx.legacy:legacy-support-v13:$LEGACY_V13"
        const val Biometric = "androidx.biometric:biometric-ktx:$BIOMETRIC_VERSION"
        const val NavigationUI = "androidx.navigation:navigation-ui-ktx:$NAVIGATION_VERSION"
        const val NavigationFragment = "androidx.navigation:navigation-fragment-ktx:$NAVIGATION_VERSION"
        const val Browser = "androidx.browser:browser:$BROWSER_VERSION"
    }

    object AndroidxTest {
        const val Runner = "androidx.test:runner:$ANDROIDX_TEST_VERSION"
        const val Rules = "androidx.test:rules:$ANDROIDX_TEST_VERSION"
        const val EspressoCore = "androidx.test.espresso:espresso-core:$ESPRESSO_VERSION"
        const val JunitKtx = "androidx.test.ext:junit-ktx:$JUNIT_KTX_VERSION"
    }

    object AndroidxCamera {
        const val Core = "androidx.camera:camera-core:$CAMERAX_VERSION"
        const val Camera2 = "androidx.camera:camera-camera2:$CAMERAX_VERSION"
        const val Lifecycle = "androidx.camera:camera-lifecycle:$CAMERAX_VERSION"
        const val View = "androidx.camera:camera-view:$CAMERAX_VIEW_VERSION"
        const val Extensions = "androidx.camera:camera-extensions:$CAMERAX_VIEW_VERSION"
    }

    object Google {
        const val ServicesPlugin = "com.google.gms:google-services:$SERVICES_PLUGIN_VERSION"
    }

    object Firebase {
        const val Messaging = "com.google.firebase:firebase-messaging-ktx:$FIREBASE_MESSAGING_VERSION"
        const val Analytics = "com.google.firebase:firebase-analytics-ktx:$FIREBASE_ANALYTICS_VERSION"
        const val ConfigKtx = "com.google.firebase:firebase-config-ktx:$FIREBASE_CONFIG_VERSION"
        const val Crashlytics = "com.google.firebase:firebase-crashlytics-ndk:$CRASHLYTICS_VERSION"
        const val FunctionsKtx = "com.google.firebase:firebase-functions-ktx:$FIREBASE_FUNCTIONS_VERSION"
        const val DistributionPlugin = "com.google.firebase:firebase-appdistribution-gradle:$FIREBASE_APPDIST_VERSION"
        const val CrashlyticsPlugin = "com.google.firebase:firebase-crashlytics-gradle:$CRASHLYTICS_PLUGIN_VERSION"
    }

    object Material {
        const val Core = "com.google.android.material:material:$MATERIAL_VERSION"
    }

    object Guava {
        const val Core = "com.google.guava:guava:$GUAVA_VERSION"
    }

    object Zxing {
        const val Core = "com.google.zxing:core:$ZXING_VERSION"
    }

    object FastAdapter {
        const val Core = "com.mikepenz:fastadapter:$FASTADAPTER_VERSION"
        const val DiffExtensions = "com.mikepenz:fastadapter-extensions-diff:$FASTADAPTER_VERSION"
        const val DragExtensions = "com.mikepenz:fastadapter-extensions-drag:$FASTADAPTER_VERSION"
        const val UtilExtensions = "com.mikepenz:fastadapter-extensions-utils:$FASTADAPTER_VERSION"
    }

    object Conductor {
        const val Core = "com.bluelinelabs:conductor:$CONDUCTOR_VERSION"
        const val ViewPager = "com.bluelinelabs:conductor-viewpager:$CONDUCTOR_VERSION"
    }

    object Kodein {
        const val CoreErasedJvm = "org.kodein.di:kodein-di-erased-jvm:$KODEIN_VERSION"
        const val FrameworkAndroidX = "org.kodein.di:kodein-di-framework-android-x:$KODEIN_VERSION"
    }

    object Mobius {
        const val Core = "com.spotify.mobius:mobius-core:$MOBIUS_VERSION"
        const val Android = "com.spotify.mobius:mobius-android:$MOBIUS_VERSION"
        const val Test = "com.spotify.mobius:mobius-test:$MOBIUS_VERSION"
    }

    object OkHttp {
        const val Core = "com.squareup.okhttp3:okhttp:$OKHTTP_VERSION"
        const val LoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$OKHTTP_VERSION"
        const val MockWebServer = "com.squareup.okhttp3:mockwebserver:$OKHTTP_VERSION"
    }

    object Networking {
        const val Moshi = "com.squareup.moshi:moshi-kotlin:$MOSHI_VERSION"
        const val MoshiAdapter = "com.squareup.moshi:moshi-adapters:$MOSHI_VERSION"
        const val MoshiCodegen = "com.squareup.moshi:moshi-kotlin-codegen:$MOSHI_VERSION"

        const val Retrofit = "com.squareup.retrofit2:retrofit:$RETROFIT_VERSION"
        const val RetrofitMoshiConverter = "com.squareup.retrofit2:converter-moshi:$RETROFIT_VERSION"
    }

    object Picasso {
        const val Core = "com.squareup.picasso:picasso:$PICASSO_VERSION"
    }

    object Glide {
        const val Core = "com.github.bumptech.glide:glide:$GLIDE_VERSION"
        const val Compiler = "com.github.bumptech.glide:compiler:$GLIDE_VERSION"
    }

    object Coroutines {
        const val Core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
        const val Android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
        const val Test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$COROUTINES_VERSION"
    }

    object Robolectric {
        const val Core = "org.robolectric:robolectric:$ROBOLECTRIC_VERSION"
    }

    object Kotlin {
        const val GradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
        const val StdLibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$KOTLIN_VERSION"
        const val Test = "org.jetbrains.kotlin:kotlin-test:$KOTLIN_VERSION"
        const val TestJunit = "org.jetbrains.kotlin:kotlin-test-junit:$KOTLIN_VERSION"
        const val TestCommon = "org.jetbrains.kotlin:kotlin-test-common:$KOTLIN_VERSION"
        const val TestAnnotationsCommon = "org.jetbrains.kotlin:kotlin-test-annotations-common:$KOTLIN_VERSION"
    }

    object Kotlinx {
        const val DateTime = "org.jetbrains.kotlinx:kotlinx-datetime:$KOTLINX_DATETIME_VERSION"
        const val SerializationRuntime = "org.jetbrains.kotlinx:kotlinx-serialization-json:$KOTLINX_SERIALIZATION_VERSION"
        const val Atomicfu = "org.jetbrains.kotlinx:atomicfu:$KOTLINX_ATOMICFU_VERSION"
    }

    object Ktor {
        object Client {
            const val Core = "io.ktor:ktor-client-core:$KTOR_VERSION"
            const val Json = "io.ktor:ktor-client-json:$KTOR_VERSION"
            const val Serialization = "io.ktor:ktor-client-serialization:$KTOR_VERSION"
            const val Logging = "io.ktor:ktor-client-logging:$KTOR_VERSION"
            const val OkHttpClientEngine = "io.ktor:ktor-client-okhttp:$KTOR_VERSION"
            const val IosClientEngine = "io.ktor:ktor-client-ios:$KTOR_VERSION"
        }
    }

    object Jetty {
        const val Webapp = "org.eclipse.jetty:jetty-webapp:$JETTY_VERSION"
        const val WebSocket = "org.eclipse.jetty.websocket:websocket-server:$JETTY_VERSION"
    }

    object LeakCanary {
        const val Core = "com.squareup.leakcanary:leakcanary-android:$LEAKCANARY_VERSION"
    }

    object AnrWatchdog {
        const val Core = "com.github.anrwatchdog:anrwatchdog:$ANR_WATCHDOG_VERSION"
    }

    object ApacheCommons {
        const val IO = "commons-io:commons-io:$COMMONS_IO_VERSION"
        const val Compress = "org.apache.commons:commons-compress:$COMMONS_COMPRESS_VERSION"
    }

    object Jbsdiff {
        const val Core = "io.sigpipe:jbsdiff:$JBSDIFF_VERSION"
    }

    object Slf4j {
        const val Api = "org.slf4j:slf4j-api:$SLF4J_VERSION"
    }

    object JUnit {
        const val Core = "junit:junit:$JUNIT_VERSION"
    }

    object Mockito {
        const val Core = "org.mockito:mockito-core:$MOCKITO_VERSION"
        const val Android = "org.mockito:mockito-android:$MOCKITO_VERSION"
    }

    object Kaspresso {
        const val Core = "com.kaspersky.android-components:kaspresso:$KASPRESSO_VERSION"
    }

    object Kakao {
        const val Core = "com.agoda.kakao:kakao:$KAKAO_VERSION"
    }

    object Detekt {
        const val Formatting = "io.gitlab.arturbosch.detekt:detekt-formatting:$DETEKT_VERSION"
    }

    object AutoDesk {
        const val CoroutineWorker = "com.autodesk:coroutineworker:$AUTODESK_VERSION"
    }

    object Checkout {
        const val Frames = "com.github.checkout:frames-android:$CHECKOUT_FRAMES_VERSION"
    }

    object Plaid {
        const val Core = "com.plaid.link:sdk-core:$PLAID_VERSION"
    }

    object Lottie {
        const val Core = "com.airbnb.android:lottie:$LOTTIE_VERSION"
    }
}
