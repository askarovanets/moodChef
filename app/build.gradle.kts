import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

android {
    buildFeatures {
        buildConfig = true
    }

    namespace = "com.example.moodchef"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.moodchef"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load API Keys from config.properties
        val configFile = rootProject.file("config.properties")
        val configProperties = Properties()

        if (configFile.exists()) {
            configFile.inputStream().use { configProperties.load(it) }
        }

        // Add Twinword and Spoonacular API keys as BuildConfig fields
        buildConfigField("String", "TWINWORD_API_KEY", "\"${configProperties["TWINWORD_API_KEY"] ?: ""}\"")
        buildConfigField("String", "SPOONACULAR_API_KEY", "\"${configProperties["SPOONACULAR_API_KEY"] ?: ""}\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
