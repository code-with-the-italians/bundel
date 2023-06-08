plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.hilt)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "dev.sebastiano.bundel.preferences"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":shared-ui"))
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.composeUiTooling)
    implementation(libs.bundles.datastore)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.lifecycle)
    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.hilt.hiltNavigationCompose)
    implementation(libs.arrow.core)
    implementation(libs.coilKt.coil.compose)
    implementation(libs.jakes.timber.timber)
    implementation(libs.kotlinx.serialization)

    kapt(libs.bundles.hiltKapt)

    testImplementation(libs.assertk)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${project.libs.versions.protobuf.get()}"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}
