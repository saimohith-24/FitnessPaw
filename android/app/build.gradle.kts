import java.io.File

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("io.gitlab.arturbosch.detekt")
}

android {
    namespace = "com.example.fitnesspaw"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fitnesspaw"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystoreFilePath = System.getenv("KEYSTORE_PATH")
            val keystorePasswordVal = System.getenv("KEYSTORE_PASSWORD")
            val keyAliasVal = System.getenv("KEY_ALIAS")
            val keyPasswordVal = System.getenv("KEY_PASSWORD")

            if (!keystoreFilePath.isNullOrEmpty() && File(keystoreFilePath).exists()) {
                storeFile = file(keystoreFilePath)
                storePassword = keystorePasswordVal
                keyAlias = keyAliasVal
                keyPassword = keyPasswordVal
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val keystoreFilePath = System.getenv("KEYSTORE_PATH")
            if (!keystoreFilePath.isNullOrEmpty() && File(keystoreFilePath).exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // CORE
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")

    // COMPOSE
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // NAVIGATION
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // DATASTORE
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")

    // WORK MANAGER
    implementation("androidx.work:work-runtime-ktx:2.10.0")

    // DEBUG
    debugImplementation("androidx.compose.ui:ui-tooling")

    // TESTING
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

detekt {
    toolVersion = "1.23.6"
    config.setFrom(files("../../testing/codequality/detekt.yml"))
    buildUponDefaultConfig = true
    allRules = false
    ignoreFailures = true
    reports {
        html.required.set(true)
        html.outputLocation.set(file("../build/reports/detekt.html"))
    }
}
