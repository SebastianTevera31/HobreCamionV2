plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.rfz.appflotal"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.rfz.appflotal"
        minSdk = 28
        targetSdk = 35
        versionCode = 5
        versionName = "1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            buildConfigField(
                "String",
                "URL_API",
                "\"https://owneroperator.azurewebsites.net/\""
            )
            buildConfigField("String", "DB_NAME", "\"AppFlotalDatabase\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "URL_API",
                "\"https://owneroperator.azurewebsites.net/\""
            )
            buildConfigField("String", "DB_NAME", "\"AppFlotalDatabase\"")
            signingConfig = signingConfigs.getByName("debug")
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
        buildConfig = true
    }
    composeOptions {

        kotlinCompilerExtensionVersion = "2.1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Se usa la Bill of Materials (BOM) de Compose para gestionar las versiones de las librerías de Compose.
    implementation(platform("androidx.compose:compose-bom:2024.02.02"))

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")

    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // Corregido: La versión la gestiona la BOM. Se quita el <version> placeholder.
    implementation("androidx.compose.material:material-icons-extended")

    implementation("androidx.work:work-runtime:2.10.2")
    // Corregido: Las versiones de WorkManager deben ser las mismas.
    implementation("androidx.work:work-runtime-ktx:2.10.2")

    implementation("com.google.android.engage:engage-core:1.5.8")
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("androidx.room:room-runtime:2.7.2")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.compose.animation:animation") // Versión gestionada por BOM
    kapt("androidx.room:room-compiler:2.7.2")

    implementation("androidx.room:room-ktx:2.7.2")

    implementation("androidx.navigation:navigation-compose:2.9.0")

    implementation("androidx.compose.runtime:runtime-livedata") // Versión gestionada por BOM

    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.github.f0ris.sweetalert:library:1.5.6")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")

    implementation("com.google.dagger:hilt-android:2.57.2")
    kapt("com.google.dagger:hilt-compiler:2.57.2")
    // Hilt ViewModels
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    implementation("com.google.accompanist:accompanist-permissions:0.32.0")

    implementation("com.github.composeuisuite:ohteepee:1.0.6")

    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.camera:camera-extensions:1.4.2")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")

    implementation("com.vanniktech:android-image-cropper:4.5.0")

    // Dependencias de Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.02"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Dependencias de Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
