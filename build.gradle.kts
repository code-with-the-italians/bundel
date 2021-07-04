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
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

apply(plugin = "com.github.ben-manes.versions")
apply(plugin = "io.gitlab.arturbosch.detekt")
apply(plugin = "org.jmailen.kotlinter")

allprojects {
    buildscript {
        repositories {
            google()
            maven {
                url = uri("https://plugins.gradle.org/m2/")
            }
        }
    }

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
