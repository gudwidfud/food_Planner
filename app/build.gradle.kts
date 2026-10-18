plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.foodplanner"
    //noinspection GradleDependency
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.foodplanner"
        minSdk = 26
        //noinspection OldTargetSdkVersion,EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
    implementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    //noinspection GradleDependency
    implementation("androidx.activity:activity-compose:1.10.1")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    //noinspection GradleDependency
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    //noinspection GradleDependency
    implementation("androidx.core:core-ktx:1.15.0")

    val room = "2.6.1"
    //noinspection GradleDependency
    implementation("androidx.room:room-runtime:$room")
    //noinspection GradleDependency
    implementation("androidx.room:room-ktx:$room")
    //noinspection GradleDependency
    ksp("androidx.room:room-compiler:$room")
}
