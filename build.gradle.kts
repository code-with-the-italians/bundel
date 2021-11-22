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

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
