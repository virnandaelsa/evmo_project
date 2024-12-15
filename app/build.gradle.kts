plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.virnandaelsa_3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.virnandaelsa_3"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.activity:activity:1.9.2")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.squareup.picasso:picasso:2.8")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.google.android.material:material:1.8.0")

    // Navigasi
    implementation ("androidx.navigation:navigation-fragment-ktx:2.5.3") // Versi terbaru
    implementation ("androidx.navigation:navigation-ui-ktx:2.5.3") // Versi terbaru

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.github.bumptech.glide:compiler:4.12.0")


    //Messaging
    implementation("com.google.firebase:firebase-messaging-ktx:24.0.2")
    implementation ("com.google.firebase:firebase-analytics-ktx")

    implementation("androidx.fragment:fragment:1.8.5")

}
