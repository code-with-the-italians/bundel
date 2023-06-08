package dev.sebastiano.bundel.preferences

import kotlinx.serialization.Serializable

class DependenciesModel : ArrayList<DependenciesModel.Dependency>() {

    @Serializable
    data class Dependency(
        val artifactId: String,
        val groupId: String,
        val name: String? = null,
        val scm: Scm? = null,
        val spdxLicenses: List<SpdxLicense>? = null,
        val unknownLicenses: List<UnknownLicense>? = null,
        val version: String
    ) {

        val coordinates: String
            get() = "$groupId:$artifactId"

        @Serializable
        data class Scm(
            val url: String
        )

        @Serializable
        data class SpdxLicense(
            val identifier: String,
            val name: String,
            val url: String
        )

        @Serializable
        data class UnknownLicense(
            val name: String,
            val url: String
        )
    }
}
