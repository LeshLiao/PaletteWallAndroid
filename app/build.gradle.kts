import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.palettex.palettewall"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.palettex.palettewall"
        minSdk = 30
        targetSdk = 35
        versionCode = 303
        versionName = "3.03"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // Try environment variables first (for CI), then fall back to local.properties
            val keystorePath = System.getenv("BITRISEIO_ANDROID_KEYSTORE_URL")
                ?: System.getenv("KEYSTORE_PATH")
            val keystorePassword = System.getenv("BITRISEIO_ANDROID_KEYSTORE_PASSWORD")
                ?: System.getenv("KEYSTORE_PASSWORD")
            val keyAliasName = System.getenv("BITRISEIO_ANDROID_KEYSTORE_ALIAS")
                ?: System.getenv("KEY_ALIAS")
            val keyAliasPassword = System.getenv("BITRISEIO_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD")
                ?: System.getenv("KEY_PASSWORD")

            if (keystorePath != null && keystorePassword != null &&
                keyAliasName != null && keyAliasPassword != null) {
                // CI environment (Bitrise)
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                keyAlias = keyAliasName
                keyPassword = keyAliasPassword
            } else {
                // Local development - try local.properties
                val localPropertiesFile = rootProject.file("local.properties")
                if (localPropertiesFile.exists()) {
                    val properties = Properties()
                    localPropertiesFile.inputStream().use { properties.load(it) }

                    storeFile = file(properties.getProperty("STORE_FILE") ?: "")
                    storePassword = properties.getProperty("STORE_PASSWORD") ?: ""
                    keyAlias = properties.getProperty("KEY_ALIAS") ?: ""
                    keyPassword = properties.getProperty("KEY_PASSWORD") ?: ""
                } else {
                    println("Warning: No signing configuration found")
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

    // Product Flavors (KTS syntax)
    flavorDimensions += "env"

    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            resValue("string", "app_name", "PaletteX-d")
            // buildConfigField("String", "BASE_URL", "\"https://dev.online-store-service.onrender.com/api/\"")
            buildConfigField("String", "BASE_URL", "\"https://online-store-service.onrender.com/api/\"")
        }

        create("staging") {
            dimension = "env"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"

            resValue("string", "app_name", "PaletteX-s")
            // buildConfigField("String", "BASE_URL", "\"https://staging.online-store-service.onrender.com/api/\"")
            buildConfigField("String", "BASE_URL", "\"https://online-store-service.onrender.com/api/\"")
        }

        create("prod") {
            dimension = "env"

            resValue("string", "app_name", "PaletteX")
            buildConfigField("String", "BASE_URL", "\"https://online-store-service.onrender.com/api/\"")
        }
    }

    // ----------------------------
    // Resource Overrides
    // ----------------------------
    sourceSets["dev"].res.srcDirs("src/dev/res")
    sourceSets["staging"].res.srcDirs("src/staging/res")
    sourceSets["prod"].res.srcDirs("src/prod/res")

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
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
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
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-config")
    implementation(libs.androidx.foundation)
    implementation(libs.coil.compose.v222)
    implementation("androidx.compose.material:material:1.5.4")
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.android.billingclient:billing-ktx:6.1.0")
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}