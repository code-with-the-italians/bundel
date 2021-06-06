import com.android.build.gradle.internal.lint.AndroidLintTask
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("io.gitlab.arturbosch.detekt")
    id("org.jmailen.kotlinter")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "dev.sebastiano.bundel"
        minSdk = 26
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildFeatures {
        compose = true
    }

    lint {
        lintConfig = rootProject.file("build-config/lint.xml")
        isWarningsAsErrors = true
        sarifReport = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = project.libs.versions.compose.get()
    }
}

detekt {
    input = files("src/main/java", "src/main/kotlin")
    config = rootProject.files("build-config/detekt.yml")
    buildUponDefaultConfig = true
    reports {
        sarif {
            enabled = true
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.activityCompose)
    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.hilt.hiltNavigationCompose)
    implementation(libs.androidx.navigation.navigationCompose)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.room)
    implementation(libs.coilKt.coil)
    implementation(libs.jakes.timber.timber)

    implementation(platform("com.google.firebase:firebase-bom:28.0.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    kapt(libs.androidx.room.roomCompiler)
    kapt(libs.bundles.hiltKapt)
}

tasks {
    withType<Detekt> {
        // Required for type resolution
        jvmTarget = "1.8"
    }

    val staticAnalysis by registering {
        val detektRelease by getting(Detekt::class)
        val androidLintRelease = named<AndroidLintTask>("lintRelease")

        dependsOn(detekt, detektRelease, androidLintRelease, lintKotlin)
    }

    register<Sync>("collectSarifReports") {
        val detektRelease by getting(Detekt::class)
        val androidLintRelease = named<AndroidLintTask>("lintRelease")

        mustRunAfter(detekt, detektRelease, androidLintRelease, lintKotlin, staticAnalysis)

        from(detektRelease.sarifReportFile) {
            rename { "detekt-release.sarif" }
        }
        from(detekt.get().sarifReportFile) {
            rename { "detekt.sarif" }
        }
        from(androidLintRelease.get().sarifReportOutputFile.get().asFile) {
            rename { "android-lint.sarif" }
        }

        into("$buildDir/reports/sarif")

        doLast {
            logger.info("Copied ${inputs.files.files.filter { it.exists() }} into ${outputs.files.files}")
            logger.info("Output dir contents:\n${outputs.files.files.first().listFiles()?.joinToString()}")
        }
    }
}
