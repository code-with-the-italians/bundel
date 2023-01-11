import com.android.build.gradle.internal.lint.AndroidLintTask
import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.shot)
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.sebastiano.bundel"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        namespace = "dev.sebastiano.bundel"
        testNamespace = "dev.sebastiano.bundel.test"

        testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
        testInstrumentationRunnerArguments += "useTestStorageService" to "true"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    lint {
        lintConfig = rootProject.file("build-config/lint.xml")
        //isWarningsAsErrors = true
        sarifReport = true
    }

    compileOptions {
        // We likely don't reeeeally need this, but hey — shiny
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packagingOptions {
        resources {
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }
}

hilt {
    enableAggregatingTask = true
}

kotlin {
    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }
}

detekt {
    source = files("src/main/java", "src/main/kotlin")
    config = rootProject.files("build-config/detekt.yml")
    buildUponDefaultConfig = true
}

val dummyGoogleServicesJson: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false

    attributes {
        attribute(Attribute.of("google.services.json", String::class.java), "dummy-json")
    }
}

dependencies {
    coreLibraryDesugaring(libs.com.android.tools.desugar)

    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.composeUiTooling)
    implementation(libs.bundles.datastore)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.room)
    implementation(libs.androidx.activity.activityCompose)
    implementation(libs.androidx.appCompat)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.hilt.hiltNavigationCompose)
    implementation(libs.androidx.navigation.navigationCompose)
    implementation(libs.coilKt.coil.compose)
    implementation(libs.jakes.timber.timber)

    // Working around a corrupted Lint rule in fragment 1.5.0 (Hilt transitive dependency)
    implementation(libs.androidx.fragment)

    implementation(platform(libs.google.firebase.bom))
    implementation(libs.bundles.firebase)

    ksp(libs.androidx.room.roomCompiler)
    kapt(libs.bundles.hiltKapt)

    debugImplementation(libs.androidx.compose.ui.uiTest.manifest)
    implementation(libs.google.android.glanceTools.host) // TODO move to debug sourceset
    debugImplementation(libs.google.android.glanceTools.viewer) // TODO move to debug sourceset

    testImplementation(kotlin("reflect"))
    testImplementation(libs.assertk)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.bundles.composeTest)
    androidTestImplementation(libs.androidx.compose.ui.uiTest.junit4)
    androidTestImplementation(libs.androidx.test.uiAutomator)

    androidTestUtil(libs.bundles.androidxTestUtils)

    dummyGoogleServicesJson(projects.bundel)
}

shot {
    applicationId = android.defaultConfig.applicationId
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${project.libs.versions.protobuf.get()}"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

open class GenerateGoogleServicesJson : DefaultTask() {

    @get:InputFiles
    var configuration by project.objects.property<Configuration>()

    @get:OutputFile
    var outputJson by project.objects.property<File>()

    @TaskAction
    fun generateJson() {
        outputJson.writeText(configuration.resolve().single().readText())
    }
}

tasks {
    withType<Detekt> {
        // Required for type resolution
        jvmTarget = "1.8"
        reports {
            sarif {
                required.set(true)
            }
        }
    }

    val lintReportReleaseSarifOutput = project.layout.buildDirectory.file("reports/sarif/lint-results-release.sarif")
    afterEvaluate {
        // Needs to be in afterEvaluate because it's not created yet otherwise
        named<AndroidLintTask>("lintReportRelease") {
            sarifReportOutputFile.set(lintReportReleaseSarifOutput)
        }
    }

    val staticAnalysis by registering {
        val detektRelease by getting(Detekt::class)
        val androidLintReportRelease = named<AndroidLintTask>("lintReportRelease")

        dependsOn(detekt, detektRelease, androidLintReportRelease, lintKotlin)
    }

    register<Sync>("collectSarifReports") {
        val detektRelease by getting(Detekt::class)
        val androidLintReportRelease = named<AndroidLintTask>("lintReportRelease")

        mustRunAfter(detekt, detektRelease, androidLintReportRelease, lintKotlin, staticAnalysis)

        from(detektRelease.sarifReportFile) {
            rename { "detekt-release.sarif" }
        }
        from(detekt.get().sarifReportFile) {
            rename { "detekt.sarif" }
        }
        from(lintReportReleaseSarifOutput) {
            rename { "android-lint.sarif" }
        }

        into("$buildDir/reports/sarif")

        doLast {
            logger.info("Copied ${inputs.files.files.filter { it.exists() }} into ${outputs.files.files}")
            logger.info("Output dir contents:\n${outputs.files.files.first().listFiles()?.joinToString()}")
        }
    }

    val copyDummyGoogleServicesJson by registering(GenerateGoogleServicesJson::class) {
        onlyIf { System.getenv("CI") == "true" }
        configuration = dummyGoogleServicesJson
        outputJson = file("google-services.json")
    }

    val checkGoogleServicesJson by registering {
        onlyIf { System.getenv("CI") != "true" }
        doLast {
            if (!project.file("google-services.json").exists()) {
                throw GradleException(
                    "You need a google-services.json file to run this project. Please refer to the CONTRIBUTING.md file for details."
                )
            }
        }
    }

    afterEvaluate {
        named("processReleaseGoogleServices")
            .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
        named("processDebugGoogleServices")
            .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
    }
}
