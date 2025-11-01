plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.tad_bank_t1"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.tad_bank_t1"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    signingConfigs {
        create("release") {
            // đường dẫn keystore bạn đã tạo ở B2
            storeFile = file("D:/keys/tadbank-release.jks")

            // KHÔNG hardcode mật khẩu: đọc từ gradle.properties hoặc ENV
            storePassword =
                (project.findProperty("TADBANK_STORE_PWD") as String?)
                    ?: System.getenv("TADBANK_STORE_PWD")
                            ?: ""

            keyAlias = "tadbank_release"

            keyPassword =
                (project.findProperty("TADBANK_KEY_PWD") as String?)
                    ?: System.getenv("TADBANK_KEY_PWD")
                            ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    //  Firebase SDKs
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore") 
    implementation("com.google.firebase:firebase-storage")

    // circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0") 

    // Google Maps and Places SDKs
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.libraries.places:places:5.0.0")
//    implementation("com.google.android.gms:play-services-location:21.3.0")

    // material design,     
    implementation ("com.google.android.material:material:1.13.0")

    // ExoPlayer and Media3
    implementation ("androidx.media3:media3-exoplayer:1.8.0")
    implementation ("androidx.media3:media3-ui:1.8.0")

    // Lottie for animations
    implementation ("com.airbnb.android:lottie:6.0.0")

    // ML Kit for text recognition and face detection
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.mlkit:face-detection:16.1.6")

    // Common dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}