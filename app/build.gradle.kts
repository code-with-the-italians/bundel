import com.android.build.gradle.internal.lint.AndroidLintTask
import com.android.build.gradle.internal.tasks.factory.dependsOn
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinter)
    alias(libs.plugins.shot)
    alias(libs.plugins.licensee)
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.sebastiano.bundel"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        namespace = "dev.sebastiano.bundel"
        testNamespace = "dev.sebastiano.bundel.test"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        testInstrumentationRunnerArguments += "useTestStorageService" to "true"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    testOptions {
        emulatorControl.enable = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }

    applicationVariants.all {
        mergeAssetsProvider.dependsOn(tasks.named("scaryEyes"))
    }
}

licensee {
    allow("Apache-2.0")
    allow("BSD-3-Clause")
    allow("MIT")
    allowUrl("https://developer.android.com/studio/terms.html")
    ignoreDependencies("io.arrow-kt") // TODO: sort out why Arrow is exploding the build
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
    source.setFrom(files("src/main/java", "src/main/kotlin"))
    config.setFrom(files("build-config/detekt.yml"))
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
    implementation(project(":shared-ui"))
    implementation(project(":preferences"))
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

    implementation(libs.kotlinx.serialization)

    testImplementation(kotlin("reflect"))
    testImplementation(libs.assertk)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.bundles.composeTest)
    androidTestImplementation(libs.google.accompanistTestHarness)
    androidTestImplementation(libs.androidx.compose.ui.uiTest.junit4)
    androidTestImplementation(libs.androidx.test.uiAutomator)
    androidTestImplementation(libs.androidx.test.espresso.device)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.tracing)

    androidTestUtil(libs.bundles.androidxTestUtils)

    dummyGoogleServicesJson(projects.bundel)
}

shot {
    applicationId = android.defaultConfig.applicationId
}

configurations.all {
    resolutionStrategy {
        force("androidx.tracing:tracing:1.1.0")
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
    val detekt = withType<Detekt> {
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
            detekt.forEach {
                from(it.sarifReportFile) {
                    rename { "detekt.sarif" }
                }
            }
            from(lintReportReleaseSarifOutput) {
                rename { "android-lint.sarif" }
            }

            into("${layout.buildDirectory}/reports/sarif")

            doLast {
                logger.info("Copied ${inputs.files.files.filter { it.exists() }} into ${outputs.files.files}")
                logger.info("Output dir contents:\n${outputs.files.files.first().listFiles()?.joinToString()}")
            }
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

    // This copies the Licensee json file to the app assets folder
    register<Copy>("scaryEyes") {
        from("${layout.buildDirectory}/reports/licensee/release/artifacts.json") {
            rename { "licences.json" }
        }
        into("$projectDir/src/main/assets")
        dependsOn("licenseeRelease")
    }

    afterEvaluate {
        named("processReleaseGoogleServices")
            .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
        named("processDebugGoogleServices")
            .dependsOn(copyDummyGoogleServicesJson, checkGoogleServicesJson)
    }
}
