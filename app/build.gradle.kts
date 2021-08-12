import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    id("com.android.application")
    id("com.mikepenz.aboutlibraries.plugin")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("kapt")
}

android {
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        applicationId = "org.xtimms.ridebus"
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "0.1"

        buildConfigField("String", "COMMIT_COUNT", "\"${getCommitCount()}\"")
        buildConfigField("String", "COMMIT_SHA", "\"${getGitSha()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }
    }

    buildTypes {
        named("debug") {
            versionNameSuffix = "-${getCommitCount()}"
            applicationIdSuffix = ".debug"

            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
        create("debugFull") { // Debug without R8
            initWith(getByName("debug"))
            isShrinkResources = false
            isMinifyEnabled = false
        }
        named("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("debugFull").res.srcDirs("src/debug/res")
    }

    flavorDimensions("default")

    productFlavors {
        create("standard") {
            dimension = "default"
        }
        create("dev") {
            resConfigs("en", "xxhdpi")
            dimension = "default"
        }
    }

    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("LICENSE.txt")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/*.kotlin_module")
    }

    dependenciesInfo {
        includeInApk = false
    }

    buildFeatures {
        viewBinding = true
    }

    lint {
        disable("MissingTranslation", "ExtraTranslation")
        isAbortOnError = false
        isCheckReleaseBuilds = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {

    implementation(kotlin("reflect", version = BuildPluginsVersion.KOTLIN))

    val coroutinesVersion = "1.5.1"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // AndroidX libraries
    implementation("androidx.annotation:annotation:1.3.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.4.0-alpha03")
    implementation("androidx.biometric:biometric-ktx:1.2.0-alpha03")
    implementation("androidx.browser:browser:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
    implementation("androidx.core:core-ktx:1.7.0-alpha01")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha01")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    val lifecycleVersion = "2.4.0-alpha01"
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // RX
    implementation("io.reactivex:rxandroid:1.2.1")
    implementation("io.reactivex:rxjava:1.3.8")

    // Database
    implementation("androidx.sqlite:sqlite-ktx:2.1.0")
    implementation("com.github.inorichi.storio:storio-common:8be19de@aar")
    implementation("com.github.inorichi.storio:storio-sqlite:8be19de@aar")
    implementation("com.github.requery:sqlite-android:3.36.0")

    // Model View Presenter
    val nucleusVersion = "3.0.0"
    implementation("info.android15.nucleus:nucleus:$nucleusVersion")
    implementation("info.android15.nucleus:nucleus-support-v7:$nucleusVersion")

    // FlowBinding
    val flowbindingVersion = "1.2.0"
    implementation("io.github.reactivecircus.flowbinding:flowbinding-android:$flowbindingVersion")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-appcompat:$flowbindingVersion")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-recyclerview:$flowbindingVersion")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-swiperefreshlayout:$flowbindingVersion")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-viewpager:$flowbindingVersion")

    // UI libraries
    implementation("com.google.android.material:material:1.5.0-alpha01")
    implementation("eu.davidea:flexible-adapter:5.1.0")
    implementation("eu.davidea:flexible-adapter-ui:1.0.0")
    implementation("com.nightlynexus.viewstatepageradapter:viewstatepageradapter:1.1.0")
    implementation("dev.chrisbanes.insetter:insetter:0.6.0")

    // Preferences
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("com.github.tfcporciuncula.flow-preferences:flow-preferences:1.4.0")

    // Dependency injection
    implementation("com.github.inorichi.injekt:injekt-core:65b0440")

    // Logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Licenses
    implementation("com.mikepenz:aboutlibraries-core:${BuildPluginsVersion.ABOUTLIB_PLUGIN}")

    // Conductor
    val conductorVersion = "3.0.0"
    implementation("com.bluelinelabs:conductor:$conductorVersion")
    implementation("com.bluelinelabs:conductor-viewpager:$conductorVersion")
    implementation("com.github.tachiyomiorg:conductor-support-preference:$conductorVersion")

    // Tests
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.assertj:assertj-core:3.16.1")
    testImplementation("org.mockito:mockito-core:1.10.19")

    val robolectricVersion = "3.1.4"
    testImplementation("org.robolectric:robolectric:$robolectricVersion")
    testImplementation("org.robolectric:shadows-play-services:$robolectricVersion")
}

tasks {
    // See https://kotlinlang.org/docs/reference/experimental.html#experimental-status-of-experimental-api(-markers)
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xuse-experimental=kotlin.ExperimentalStdlibApi",
            "-Xuse-experimental=kotlinx.coroutines.FlowPreview",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xuse-experimental=kotlinx.coroutines.InternalCoroutinesApi",
            "-Xuse-experimental=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xuse-experimental=coil.annotation.ExperimentalCoilApi",
        )
    }

    preBuild {
        dependsOn(formatKotlin)
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = BuildPluginsVersion.KOTLIN))
    }
}

// Git is needed in your system PATH for these commands to work.
// If it's not installed, you can return a random value as a workaround
fun getCommitCount(): String {
    return runCommand("git rev-list --count HEAD")
    // return "1"
}

fun getGitSha(): String {
    return runCommand("git rev-parse --short HEAD")
    // return "1"
}

fun getBuildTime(): String {
    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
    df.timeZone = TimeZone.getTimeZone("UTC")
    return df.format(Date())
}

fun runCommand(command: String): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        commandLine = command.split(" ")
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}