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
        classpath(libs.gradlePlugins.versionsBenManes)
    }

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

apply(plugin = "com.github.ben-manes.versions")
apply(plugin = "io.gitlab.arturbosch.detekt")
apply(plugin = "org.jmailen.kotlinter")

allprojects {
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
