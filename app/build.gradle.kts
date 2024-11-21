plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") // Plugin de Firebase
}

android {
    namespace = "com.example.gestiondeclavesegura"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.gestiondeclavesegura"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // Importar la BoM de Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation ("androidx.biometric:biometric:1.2.0-alpha05")
    // Dependencias de Firebase (sin versiones específicas porque usamos la BoM)
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore") // Firebase Firestore
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics (opcional)

    // Dependencias estándar del proyecto
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.biometric)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
