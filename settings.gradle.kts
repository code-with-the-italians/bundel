pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }

}
rootProject.name = "Bundel"

include(":app")

enableFeaturePreview("VERSION_CATALOGS")
