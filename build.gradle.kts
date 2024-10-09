import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.versionsBenManes)
    alias(libs.plugins.versionCatalogUpdate)
    alias(libs.plugins.android.application) apply false // https://youtrack.jetbrains.com/issue/KT-31643/Unable-to-load-class-com.android.build.gradle.BaseExtension-with-Kotlin-plugin-applied-to-root-Gradle-project
    alias(libs.plugins.android.library) apply false // see above url
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.paparazzi) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false // fun https://github.com/google/dagger/issues/3068
}

//subprojects { parent!!.path.takeIf { it != rootProject.path }?.let { evaluationDependsOn(it)  } }

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

subprojects {
    buildscript {
        repositories {
            google()
            gradlePluginPortal()
            mavenCentral()
        }
    }

    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://androidx.dev/snapshots/builds/7913448/artifacts/repository") }
    }
}

val dummyGoogleServices: Configuration by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false

    attributes {
        attribute(Attribute.of("google.services.json", String::class.java), "dummy-json")
    }
}

dependencies {
    dummyGoogleServices(files(rootProject.file("build-config/dummy-data/dummy-google-services.json")))
}

tasks.withType<DependencyUpdatesTask> {
    resolutionStrategy {
        componentSelection {
            all {
                when {
                    isNonStable(candidate.version) && !isNonStable(currentVersion) -> {
                        reject("Updating stable to non stable is not allowed")
                    }
                    candidate.module == "kotlin-gradle-plugin" && candidate.version != libs.versions.kotlin.get() -> {
                        reject("Keep Kotlin version on the version specified in libs.versions.toml")
                    }
                    // KSP versions are compound versions, starting with the kotlin version
                    candidate.group == "com.google.devtools.ksp" && !candidate.version.startsWith(libs.versions.kotlin.get()) -> {
                        reject("KSP needs to stick to Kotlin version")
                    }
                }
            }
        }
    }
}
