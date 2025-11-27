import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ma.tehro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ma.tehro"
        minSdk = 21
        targetSdk = 36
        versionCode = 11
        versionName = "1.0.0"

        val localPropertiesFile = File(rootDir, "local.properties")
        val properties = Properties()
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        val fields = mapOf(
            "github_token" to properties.getProperty("github_token", ""),
            "stations_gist_id" to properties.getProperty("stations_gist_id", ""),
            "feedbacks_gist_id" to properties.getProperty("feedbacks_gist_id", "")
        )
        //noinspection WrongGradleMethod
        fields.forEach { (name, value) ->
            buildConfigField("String", name, "\"$value\"")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        vectorDrawables.useSupportLibrary = true
        androidResources.localeFilters += listOf("en")
    }

    val localProperties = gradleLocalProperties(rootDir, providers)

    signingConfigs {
        val releaseKeystorePath = localProperties.getProperty("store_file")?.takeIf { it.isNotBlank() }
        val fallbackKeystore = File(rootDir, "debug.keystore")

        if (releaseKeystorePath != null) {
            val releaseKeystore = file(releaseKeystorePath)
            if (releaseKeystore.exists()) {
                create("release") {
                    storeFile = releaseKeystore
                    storePassword = localProperties.getProperty("store_password")
                    keyAlias = localProperties.getProperty("key_alias")
                    keyPassword = localProperties.getProperty("key_password")
                }
            }
        }

        if (signingConfigs.findByName("release") == null && fallbackKeystore.exists()) {
            create("fallback") {
                storeFile = fallbackKeystore
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = when {
                signingConfigs.findByName("release") != null -> {
                    println("using real signing config for release.")
                    signingConfigs.getByName("release")
                }
                signingConfigs.findByName("fallback") != null -> {
                    println("using fallback debug signing for release.")
                    signingConfigs.getByName("fallback")
                }
                else -> {
                    println("no signing config found. release may fail.")
                    null
                }
            }
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
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    composeCompiler {
        reportsDestination = layout.buildDirectory.dir("compose_compiler")
        metricsDestination = layout.buildDirectory.dir("compose_compiler")
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
//    implementation(libs.androidx.material3)
    implementation(libs.material3)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.serialization)

    implementation(libs.androidx.navigation)
    implementation(libs.androidx.navigation.ui)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.osmdroid.android)

    implementation(libs.kotlinx.datetime)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.datastore.preferences)
}