import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import de.fayard.refreshVersions.core.versionFor
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

val verName = "1.0"
val gitCommitCount = "git rev-list HEAD --count".execute().toInt()
val gitCommitHash = "git rev-parse --verify --short HEAD".execute()

fun String.execute(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

android {
    compileSdk = 32

    namespace = "su.akari.mnjtech"

    defaultConfig {
        applicationId = "su.akari.mnjtech"
        minSdk = 23

        targetSdk = 32

        versionCode = gitCommitCount
        versionName = verName

        if (properties.getProperty("buildWithGitSuffix").toBoolean()) versionNameSuffix =
            ".r${gitCommitCount}.${gitCommitHash}"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    val config = properties.getProperty("storeFile")?.let {
        signingConfigs.create("config") {
            storeFile = file(it)
            storePassword = properties.getProperty("storePassword")
            keyAlias = properties.getProperty("keyAlias")
            keyPassword = properties.getProperty("keyPassword")
        }
    }

    buildTypes {
        all {
            signingConfig = config ?: signingConfigs["debug"]
        }

        getByName("release") {
            ndk {
                abiFilters.add("arm64-v8a")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.ui)
    }
    packagingOptions {
        resources {
            excludes += arrayOf("META-INF/**", "kotlin/**", "google/**", "**.bin")
        }
    }
}

kotlin {
    sourceSets {
        debug {
            kotlin.srcDir("build/generated/ksp/debug/kotlin")
        }
        release {
            kotlin.srcDir("build/generated/ksp/release/kotlin")
        }
    }
}

android.applicationVariants.all {
    outputs.all {
        (this as BaseVariantOutputImpl).outputFileName = "${rootProject.name}-v${versionName}-${name}.apk"
    }
}

dependencies {
    // Desugar
    coreLibraryDesugaring(Android.tools.desugarJdkLibs)

    // AndroidX
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.lifecycle.runtimeKtx)
    implementation(AndroidX.activity.compose)

    // Compose
    implementation(AndroidX.compose.ui)
    implementation(AndroidX.compose.ui.tooling)
    implementation(AndroidX.compose.ui.util)

    // Material design
    implementation(AndroidX.compose.material3)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.material3.windowSizeClass)

    // Navigation
    implementation(AndroidX.navigation.compose)

    // Md3 Compat
    implementation(Libs.md3_compat) {
        exclude(group = "com.tencent", "mmkv")
    }

    // Bottom Sheet
    implementation(Libs.sheets)

    // xCrash
    implementation(Libs.xcrash)

    // Aria
    implementation(Libs.aria_core)
    kapt(Libs.aria_compiler)

    //ExoPlayer
    implementation(Libs.exoplayer_core)
    implementation(Libs.exoplayer_ui)
    implementation(Libs.exoplayer_extension_okhttp)

    // Splash screen
    implementation(AndroidX.core.splashscreen)

    // WindowManager
    implementation(AndroidX.window.java)

    // Motion Animation
    implementation(Libs.material_motion_compose)

    // Lottie
    implementation(Libs.lottie_compose)

    // Paging3
    implementation(AndroidX.paging.runtimeKtx)
    implementation(AndroidX.paging.compose)

    // Koin
    implementation(Koin.core)
    implementation(Koin.android)
    implementation(Koin.compose)

    // Coil
    implementation(COIL.compose)
    implementation(COIL.svg)

    // Accompanist
    implementation(Google.accompanist.pager)
    implementation(Google.accompanist.pager.indicators)
    implementation(Google.accompanist.swiperefresh)
    implementation(Google.accompanist.flowlayout)
    implementation(Libs.accompanist_placeholder_material)
    implementation(Libs.accompanist_navigation_animation)
    implementation(Libs.accompanist_webview)

    // Jsoup
    implementation(Libs.jsoup)

    // Okhttp3
    implementation(Square.OkHttp3.okHttp)
    implementation(Square.OkHttp3.loggingInterceptor)

    // Retrofit2
    implementation(Square.retrofit2.retrofit)
    implementation(Square.retrofit2.converter.gson)

    // DataStore
    implementation(AndroidX.dataStore.preferences)

    // Room
    implementation(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)
    implementation(AndroidX.room.ktx)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-opt-in=coil.annotation.ExperimentalCoilApi",
            "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi"
        )

        if (name.contains("release", true)) {
            freeCompilerArgs = freeCompilerArgs + listOf(
                "-Xassertions=always-disable",
                "-Xno-param-assertions",
                "-Xno-call-assertions",
                "-Xno-receiver-assertions",
                "-opt-in=kotlin.RequiresOptIn"
            )
        }
    }
}