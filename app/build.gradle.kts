plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("F:\\Keys\\keySchedule.jks")
            storePassword = "pAaCsBsHw34o78rdDSMcHh5e8dDu9le"
            keyPassword = "pAaCsBsHw34o78rdDSMcHh5e8dDu9le"
            keyAlias = "keySchedule"
        }
        create("release") {
            storeFile = file("F:\\Keys\\keySchedule.jks")
            storePassword = "pAaCsBsHw34o78rdDSMcHh5e8dDu9le"
            keyPassword = "pAaCsBsHw34o78rdDSMcHh5e8dDu9le"
            keyAlias = "keySchedule"
        }
    }
    namespace = "com.helpful.stuSchedule"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.helpful.stuSchedule"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.navigation:navigation-ui-ktx:latest.release")
    implementation("androidx.navigation:navigation-fragment-ktx:latest.release")
}