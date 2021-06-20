plugins {
    id("io.gitlab.arturbosch.detekt") version "1.17.1" apply false
    id("org.jmailen.kotlinter") version "3.4.4" apply false
    id("com.github.ben-manes.versions") version "0.39.0"
}

buildscript {
    dependencies {
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs") as org.gradle.accessors.dm.LibrariesForLibs

        classpath(libs.gradlePlugins.android)
        classpath(libs.gradlePlugins.crashlytics)
        classpath(libs.gradlePlugins.gms)
        classpath(libs.gradlePlugins.hilt)
        classpath(libs.gradlePlugins.junit5)
        classpath(libs.gradlePlugins.kotlin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks {

    register("copyDummyDataForCi", Copy::class.java) {
        from(rootProject.file("build-config/dummy-data/dummy-google-services.json"))
        destinationDir = project(":app").projectDir
        rename { "google-services.json" }
    }
}
