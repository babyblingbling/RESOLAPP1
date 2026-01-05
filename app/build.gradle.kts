plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
}


ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

android {
    namespace = "com.example.resol" // Giữ nguyên để khớp với package code
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.resol" // Đổi thành tên mới RESOL
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    } // --- KẾT THÚC defaultConfig TẠI ĐÂY ---

    // Các khối dưới đây phải nằm NGOÀI defaultConfig
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    lint {
        checkReleaseBuilds = true
        abortOnError = false
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- FIREBASE ---
    // Chỉ dùng 1 phiên bản BOM mới nhất (33.16.0)
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database-ktx")

    // Google Auth & Coroutines
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // --- ANDROID CORE & KTX ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7") // Cập nhật version ổn định
    implementation("androidx.startup:startup-runtime:1.2.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    // --- COMPOSE UI ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Icon mở rộng cho trình phát nhạc
    implementation("androidx.compose.material:material-icons-extended:1.7.6")

    // --- NAVIGATION ---
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // --- MEDIA3 (EXOPLAYER) ---
    implementation("androidx.media3:media3-exoplayer:1.5.0")
    implementation("androidx.media3:media3-ui:1.5.0")
    implementation("androidx.media3:media3-session:1.5.0")

    // --- NEWPIPE EXTRACTOR (Lõi tải nhạc) ---
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.24.6")

    // --- ROOM DATABASE ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)


    // --- WORK MANAGER (Tải ngầm) ---
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // --- IMAGES & UTILS ---
    implementation("io.coil-kt:coil-compose:2.7.0")
    implementation("com.kmpalette:kmpalette-core:3.1.0")
    implementation("com.google.guava:guava:33.3.1-android")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Desugaring (Hỗ trợ Java mới trên Android cũ)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs_nio:2.0.4")

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}