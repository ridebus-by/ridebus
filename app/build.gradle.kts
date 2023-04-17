import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    id("com.mikepenz.aboutlibraries.plugin")
    kotlin("android")
    kotlin("plugin.serialization")
    kotlin("kapt")
    id("com.github.zellius.shortcut-helper")
}

val acraAuthLogin: String = gradleLocalProperties(rootDir).getProperty("authLogin") ?: "\"acra_login\""
val acraAuthPassword: String = gradleLocalProperties(rootDir).getProperty("authPassword") ?: "\"acra_password\""

shortcutHelper.setFilePath("./shortcuts.xml")

val SUPPORTED_ABIS = setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

android {
    namespace = "org.xtimms.ridebus"
    compileSdk = AndroidConfig.compileSdk
    ndkVersion = AndroidConfig.ndk

    defaultConfig {
        applicationId = "org.xtimms.ridebus"
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
        versionCode = 3
        versionName = "0.3"

        buildConfigField("String", "COMMIT_COUNT", "\"${getCommitCount()}\"")
        buildConfigField("String", "COMMIT_SHA", "\"${getGitSha()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildTime()}\"")
        buildConfigField("boolean", "INCLUDE_UPDATER", "false")
        buildConfigField("String", "DEVELOPER_EMAIL", "\"mailto:xtimms@live.com\"")
        buildConfigField("String", "DATABASE_VERSION", "\"3.0\"")

        // Please disable ACRA or use your own instance in forked versions of the project
        buildConfigField("String", "ACRA_URI", "\"https://acra.kotatsu.app/report\"")
        buildConfigField("String", "ACRA_AUTH_LOGIN", acraAuthLogin)
        buildConfigField("String", "ACRA_AUTH_PASSWORD", acraAuthPassword)

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true",
                    "room.expandProjection" to "true"
                )
            }
        }

        ndk {
            abiFilters += SUPPORTED_ABIS
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    splits {
        abi {
            isEnable = true
            reset()
            include(*SUPPORTED_ABIS.toTypedArray())
            isUniversalApk = true
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

    flavorDimensions.add("default")

    productFlavors {
        create("play") {
            dimension = "default"
        }
        create("standard") {
            buildConfigField("boolean", "INCLUDE_UPDATER", "true")
            dimension = "default"
        }
        create("dev") {
            resourceConfigurations += setOf("xxhdpi")
            dimension = "default"
        }
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "LICENSE.txt",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/README.md",
                "META-INF/NOTICE",
                "META-INF/*.kotlin_module",
                "META-INF/*.version"
            )
        )
    }

    dependenciesInfo {
        includeInApk = false
    }

    buildFeatures {
        viewBinding = true

        // Disable some unused things
        aidl = false
        renderScript = false
        shaders = false
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
        abortOnError = false
        checkReleaseBuilds = false
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

    val coroutinesVersion = "1.6.4"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // AndroidX libraries
    implementation("androidx.annotation:annotation:1.4.0-alpha02")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("androidx.browser:browser:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.0-alpha02")
    implementation("androidx.recyclerview:recyclerview:1.3.0-beta02")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    val lifecycleVersion = "2.5.1"
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    // Data serialization (JSON, protobuf)
    val kotlinSerializationVersion = "1.3.3"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinSerializationVersion")
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$kotlinSerializationVersion"
    )

    // Job scheduling
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.google.guava:guava:31.1-android")
    implementation(
        "com.github.Koitharu.pausing-coroutine-dispatcher:pausing-coroutine-dispatcher:5213d53420"
    )

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.10")

    // Files
    implementation("com.github.seven332:unifile:1.0.0")

    // RX
    implementation("io.reactivex:rxandroid:1.2.1")
    implementation("io.reactivex:rxjava:1.3.8")
    implementation("com.jakewharton.rxrelay:rxrelay:1.2.0")

    // Room
    val roomVersion = "2.3.0"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // Model View Presenter
    val nucleusVersion = "3.0.0"
    implementation("info.android15.nucleus:nucleus:$nucleusVersion")
    implementation("info.android15.nucleus:nucleus-support-v7:$nucleusVersion")

    // FlowBinding
    val flowbindingVersion = "1.2.0"
    implementation("io.github.reactivecircus.flowbinding:flowbinding-android:$flowbindingVersion")
    implementation("io.github.reactivecircus.flowbinding:flowbinding-appcompat:$flowbindingVersion")
    implementation(
        "io.github.reactivecircus.flowbinding:flowbinding-recyclerview:$flowbindingVersion"
    )
    implementation(
        "io.github.reactivecircus.flowbinding:flowbinding-swiperefreshlayout:$flowbindingVersion"
    )
    implementation("io.github.reactivecircus.flowbinding:flowbinding-viewpager:$flowbindingVersion")

    // UI libraries
    implementation("com.google.android.material:material:1.8.0")
    implementation("com.github.arkon.FlexibleAdapter:flexible-adapter:c8013533")
    implementation("com.github.arkon.FlexibleAdapter:flexible-adapter-ui:c8013533")
    implementation("dev.chrisbanes.insetter:insetter:0.6.1")
    implementation("com.github.vipulasri:timelineview:1.1.5")

    // Preferences
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("com.fredporciuncula:flow-preferences:1.8.0")

    // Map
    implementation("com.yandex.android:maps.mobile:4.2.2-full")

    // Dependency injection
    implementation("com.github.inorichi.injekt:injekt-core:65b0440")

    // Logging
    implementation("com.squareup.logcat:logcat:0.1")

    // Licenses
    implementation("com.mikepenz:aboutlibraries-core:${BuildPluginsVersion.ABOUTLIB_PLUGIN}")

    // Conductor
    val conductorVersion = "3.1.7"
    implementation("com.bluelinelabs:conductor:$conductorVersion")
    implementation("com.bluelinelabs:conductor-viewpager:$conductorVersion")
    implementation("com.github.tachiyomiorg:conductor-support-preference:$conductorVersion")

    // Crash reports/analytics
    implementation("ch.acra:acra-http:5.9.7")

    implementation("org.jsoup:jsoup:1.15.3")

    // Markdown
    implementation("io.noties.markwon:core:4.6.2")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
    }

    // See https://kotlinlang.org/docs/reference/experimental.html#experimental-status-of-experimental-api(-markers)
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs += listOf(
            "-opt-in=kotlin.Experimental",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlin.ExperimentalStdlibApi",
            "-opt-in=kotlinx.coroutines.FlowPreview",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-opt-in=kotlinx.coroutines.InternalCoroutinesApi",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-opt-in=coil.annotation.ExperimentalCoilApi",
            "-Xjvm-default=enable"
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
