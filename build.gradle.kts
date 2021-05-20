plugins {
    id("io.gitlab.arturbosch.detekt") version "1.16.0" apply false
    id("org.jmailen.kotlinter") version "3.4.4" apply false
}

buildscript {
    val hiltVersion by extra("2.35.1")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        classpath("com.android.tools.build:gradle:7.0.0-beta01")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath ("com.google.gms:google-services:4.3.8")
        classpath ("com.google.firebase:firebase-crashlytics-gradle:2.6.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}
