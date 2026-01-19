import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.skie)
    alias(libs.plugins.ksp)
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        // [변경 2] 현대적인 DSL 문법 적용 (getting 제거)
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)
            implementation(libs.maps.compose)
            implementation(libs.play.services.maps)

            // Room Runtime (Android 전용)
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)

            implementation(libs.androidx.lifecycle.runtimeKtx)
            implementation(libs.androidx.security.crypto) // EncryptedSharedPreferences
            implementation(libs.androidx.core.splashscreen) // SplashScreen API (Android 12+)

            // Ktor Client (Android 전용)
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.datetime)
            implementation(projects.shared)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

val signingProperties = Properties().apply {
    val propFile = rootProject.file("signing.properties")
    if (propFile.exists()) {
        propFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "good.space.runnershi"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    // API Key 및 Base URL 로딩 로직
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }
    val mapsApiKey = localProperties.getProperty("MAPS_API_KEY", "")
    // 기본값: 에뮬레이터에서 개발 머신의 localhost 접근용 (10.0.2.2)
    val baseUrl = localProperties.getProperty("BASE_URL", "http://10.0.2.2:8080")

    defaultConfig {
        applicationId = "good.space.runnershi"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.0.1"

        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey

        // BuildConfig에 BASE_URL 주입
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    signingConfigs {
        create("release") {
            // properties 파일에서 읽어온 경로를 바탕으로 파일 객체 생성
            val path = signingProperties.getProperty("STORE_FILE")
            storeFile = if (path != null) file(path) else null

            storePassword = signingProperties.getProperty("KEY_STORE_PASSWORD")
            keyAlias = signingProperties.getProperty("KEY_ALIAS")
            keyPassword = signingProperties.getProperty("KEY_PASSWORD")
        }
    }

    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true // 코드 최적화 및 난독화 활성화
            isShrinkResources = true // 사용하지 않는 리소스 제거
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

dependencies {
    debugImplementation(compose.uiTooling)

    // Room Compiler를 KSP로 설정 (Android 타겟용)
    // "kspAndroid"라고 명시하면 Android 빌드 시에만 동작합니다.
    add("kspAndroid", libs.androidx.room.compiler)
}
