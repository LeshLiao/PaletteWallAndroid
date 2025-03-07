 import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.palettex.palettewall"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.palettex.palettewall"
        minSdk = 30
        targetSdk = 34
        versionCode = 212
        versionName = "2.12"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


        android {
            signingConfigs {
                create("release") {
                    val localPropertiesFile = rootProject.file("local.properties")
                    if (localPropertiesFile.exists()) {
                        val properties = Properties()
                        localPropertiesFile.inputStream().use { properties.load(it) }

                        storeFile = file(properties.getProperty("STORE_FILE") ?: "")
                        storePassword = properties.getProperty("STORE_PASSWORD") ?: ""
                        keyAlias = properties.getProperty("KEY_ALIAS") ?: ""
                        keyPassword = properties.getProperty("KEY_PASSWORD") ?: ""
                    } else {
                        println("Warning: local.properties file is missing.")
                    }
                }
            }
        }


    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG_MODE", "true")
            isDebuggable = true
        }
        release {
             signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "DEBUG_MODE", "false")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation(libs.play.services.ads.lite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(kotlin("script-runtime"))

    implementation(libs.play.services.ads)

    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-config")

    implementation (libs.androidx.foundation)
    implementation (libs.coil.compose.v222)

    implementation ("androidx.compose.material:material:1.5.4")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    implementation ("com.android.billingclient:billing-ktx:6.1.0")
}