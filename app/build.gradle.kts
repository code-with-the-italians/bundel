plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "dev.sebastiano.bundel"
        minSdk = 26
        targetSdk = 30
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
        freeCompilerArgs = listOf(
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta01"
    }
}

dependencies {
    val hiltVersion = "2.35.1"

    implementation("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.compose.foundation:foundation:1.0.0-beta05")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta05")
    implementation("androidx.compose.material:material:1.0.0-beta05")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-beta05")
    implementation("androidx.compose.ui:ui-tooling:1.0.0-beta05")
    implementation("androidx.compose.ui:ui:1.0.0-beta05")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.4.0-alpha01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04")
    implementation("androidx.navigation:navigation-compose:1.0.0-alpha10")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha01")
}
