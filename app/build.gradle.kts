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
        versionCode = 1
        versionName = "0.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    // versionCode: Bitrise sets the actual versionCode
    // versionName: Bitrise injects versionName from Git tag(set manually)

    signingConfigs {
        create("release") {
            // local builds: Only configure signing if keystore file exists
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                val properties = Properties()
                localPropertiesFile.inputStream().use { properties.load(it) }

                val keystorePath = properties.getProperty("STORE_FILE")
                if (keystorePath != null && File(keystorePath).exists()) {
                    storeFile = file(keystorePath)
                    storePassword = properties.getProperty("STORE_PASSWORD") ?: ""
                    keyAlias = properties.getProperty("KEY_ALIAS") ?: ""
                    keyPassword = properties.getProperty("KEY_PASSWORD") ?: ""
                }
            }
            // If no local.properties, signing config will be empty
            // Bitrise CD: The Android Sign step will handle signing in CI
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEBUG_MODE", "true")
            isDebuggable = true
        }
        release {
            // local builds: Only use signing config if keystore is configured
            // Bitrise won't sign it on "Android Build" step.
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                val properties = Properties()
                localPropertiesFile.inputStream().use { properties.load(it) }
                val keystorePath = properties.getProperty("STORE_FILE")
                if (keystorePath != null && File(keystorePath).exists()) {
                    signingConfig = signingConfigs.getByName("release")
                }
            }

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