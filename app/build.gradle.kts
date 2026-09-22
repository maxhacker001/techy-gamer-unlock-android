plugins {
    id("com.android.application")
}

android {
    namespace = "com.techygamer.unlock"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.techygamer.unlock"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
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
}
