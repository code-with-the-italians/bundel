import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    dependencies {
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs") as org.gradle.accessors.dm.LibrariesForLibs
        classpath(libs.gradlePlugins.android)
        classpath(libs.gradlePlugins.crashlytics)
        classpath(libs.gradlePlugins.detekt)
        classpath(libs.gradlePlugins.gms)
        classpath(libs.gradlePlugins.hilt)
        classpath(libs.gradlePlugins.junit5)
        classpath(libs.gradlePlugins.kotlin)
        classpath(libs.gradlePlugins.kotlinter)
        classpath(libs.gradlePlugins.protobuf)
        classpath(libs.gradlePlugins.versionCatalogUpdates)
        classpath(libs.gradlePlugins.versionsBenManes)
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

apply(plugin = "com.github.ben-manes.versions")
apply(plugin = "nl.littlerobots.version-catalog-update")
apply(plugin = "io.gitlab.arturbosch.detekt")
apply(plugin = "org.jmailen.kotlinter")

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
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
