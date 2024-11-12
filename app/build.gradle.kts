import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.trainbites"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trainbites"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        // add this extra line in gradle code
        vectorDrawables.useSupportLibrary= true


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    //extra dependencies
    implementation("com.airbnb.android:lottie:3.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.google.android.material:material:1.3.0")
    implementation("com.karumi:dexter:6.2.3")
    implementation("com.squareup.picasso:picasso:2.8")
    //implementation("com.cepheuen.elegant-number-button:lib:1.0.3")
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation ("androidx.navigation:navigation-compose:2.7.7-alpha10")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.vanniktech:android-image-cropper:4.5.0")

    implementation ("com.google.firebase:firebase-messaging:23.0.0")


    // Stripe dependencies
    implementation ("com.stripe:stripe-java:26.0.0")
    implementation ("com.stripe:stripe-android:20.20.0")

    // Volley for network operations
    implementation ("com.android.volley:volley:1.2.0")

    // Lifecycle and Startup dependencies
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation ("androidx.startup:startup-runtime:1.1.1")

    // Multidex
    implementation ("androidx.multidex:multidex:2.0.1")


}