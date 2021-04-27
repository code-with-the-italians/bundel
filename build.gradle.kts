buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        classpath("com.android.tools.build:gradle:7.0.0-alpha14")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
}
