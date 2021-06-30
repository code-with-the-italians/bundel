pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "Bundel"

include(":app")

enableFeaturePreview("VERSION_CATALOGS")
include(":protobufs")
