plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "dev.sebastiano.bundel"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta01"
    }
}

dependencies {
    implementation("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.compose.foundation:foundation:1.0.0-beta05")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta05")
    implementation("androidx.compose.material:material:1.0.0-beta05")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta05")
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta05")
    implementation("androidx.compose.ui:ui:1.0.0-beta05")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha10")
    implementation("com.jakewharton.timber:timber:4.7.1")
}
