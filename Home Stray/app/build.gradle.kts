plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

val imgbbApiKey = project.findProperty("IMAGEBB_API_KEY") as? String ?: "MISSING_IMAGEBB_API_KEY"
val googleCloudApiKey = project.findProperty("GOOGLE_CLOUD_API") as? String ?: "MISSING_GOOGLE_CLOUD_API"
val adminUserId = project.findProperty("ADMIN_USER_ID") as? String ?: "MISSING_ADMIN_USER_ID"

android {
    namespace = "com.example.homestray"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.homestray"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "IMAGEBB_API_KEY", "\"$imgbbApiKey\"")
        buildConfigField("String", "GOOGLE_CLOUD_API", "\"$googleCloudApiKey\"")
        buildConfigField("String", "ADMIN_USER_ID", "\"$adminUserId\"")

    }

    buildFeatures {
        buildConfig = true
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
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.cardstackview)
    implementation(libs.glide)
    implementation(libs.play.services.location)
    implementation(libs.firebase.storage)
    annotationProcessor(libs.compiler)
    implementation(libs.okhttp)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}