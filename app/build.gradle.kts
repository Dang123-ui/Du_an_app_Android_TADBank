    import java.io.FileInputStream
    import java.util.Properties

    //plugins {
    //    alias(libs.plugins.android.application)
    //    id("com.google.gms.google-services")
    //}
    plugins {
        alias(libs.plugins.android.application)
        id("com.google.gms.google-services")
        alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    }

    // Đọc file local.properties
    val localProps = Properties()
    val localPropsFile = rootProject.file("local.properties")
    if (localPropsFile.exists()) {
        localProps.load(FileInputStream(localPropsFile))
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
//            manifestPlaceholders["MAPS_API_KEY"] = MAPS_API_KEY
//            buildConfigField("String", "SMTP_HOST", "\"${localProps.getProperty("SMTP_HOST","")}\"")
//            buildConfigField("int",    "SMTP_PORT",  "${localProps.getProperty("SMTP_PORT","587")}")
//            buildConfigField("String", "SMTP_USER", "\"${localProps.getProperty("SMTP_USER","")}\"")
//            buildConfigField("String", "SMTP_PASS", "\"${localProps.getProperty("SMTP_PASS","")}\"")
//            buildConfigField("String", "SMTP_FROM_NAME", "\"${localProps.getProperty("SMTP_FROM_NAME","TAD Bank")}\"")
        }

        packaging {
            resources {
                excludes += "META-INF/NOTICE.md"
                excludes += "META-INF/LICENSE.md"
                excludes += "META-INF/LICENSE-notice.md"
                excludes += "META-INF/DEPENDENCIES"
                excludes += "META-INF/NOTICE"
                excludes += "META-INF/NOTICE.txt"
                excludes += "META-INF/LICENSE.txt"
                excludes += "META-INF/ASL2.0"
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        signingConfigs {
            create("release") {
                storeFile = file("D:/keys/tadbank-release.jks")
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

        buildFeatures {
            viewBinding = true
            buildConfig = true
            mlModelBinding = true
        }

        secrets {
            // To add your Maps API key to this project:
            // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
            // 2. Add this line, where YOUR_API_KEY is your API key:
            //        MAPS_API_KEY=YOUR_API_KEY
    //        propertiesFileName = "secrets.properties"

            // A properties file containing default secret values. This file can be
            // checked in version control.
            defaultPropertiesFileName = "local.properties"
        }
    }

    dependencies {
        //  Firebase SDKs
        implementation(platform("com.google.firebase:firebase-bom:34.5.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-auth")
        implementation("com.google.firebase:firebase-firestore")
        implementation("com.google.firebase:firebase-storage")

        // skimmer skeleton loader
        implementation("com.facebook.shimmer:shimmer:0.5.0")

        // circle image view
        implementation("de.hdodenhof:circleimageview:3.1.0")

        // Google Maps and Places SDKs

        implementation(libs.play.services.maps)
        implementation(libs.play.services.location)
    //    implementation("com.google.android.gms:play-services-maps:19.2.0")
    //    implementation("com.google.android.libraries.places:places:5.0.0")
    //    implementation("com.google.android.gms:play-services-location:21.3.0")

        // material design,
    //    implementation ("com.google.android.material:material:1.13.0")

        // ExoPlayer and Media3
        implementation ("androidx.media3:media3-exoplayer:1.8.0")
        implementation ("androidx.media3:media3-ui:1.8.0")

        // Lottie for animations
        // implementation ("com.airbnb.android:lottie:6.0.0")
        implementation ("com.airbnb.android:lottie:6.6.10")

        // ML Kit for text recognition and face detection
        implementation("com.google.mlkit:text-recognition:16.0.1")
        implementation("com.google.mlkit:face-detection:16.1.7")
        implementation("com.google.mlkit:barcode-scanning:17.3.0")

        // Common dependencies
        // TensorFlow Lite for on-device machine learning
        implementation("org.tensorflow:tensorflow-lite:2.17.0")
        implementation("org.tensorflow:tensorflow-lite-support:0.5.0")
        implementation("org.tensorflow:tensorflow-lite-gpu:2.17.0")

        // JavaMail API for email functionality
        implementation("com.sun.mail:android-mail:1.6.8")
        implementation("com.sun.mail:android-activation:1.6.8")

        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.fragment)
        implementation(libs.constraintlayout)
        implementation(libs.navigation.fragment)
        implementation(libs.navigation.ui)
        implementation(libs.androidx.navigation.fragment)
        implementation(libs.androidx.navigation.ui)
        implementation (libs.media3.exoplayer)
        implementation (libs.media3.ui)
        implementation(libs.firebase.database)
        implementation(libs.tensorflow.lite.metadata)
        implementation(libs.cardview)
        implementation(libs.androidx.leanback)
        implementation(libs.glide)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }
