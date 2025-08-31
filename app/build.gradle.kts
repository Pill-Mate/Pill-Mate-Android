import java.util.*

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.gms.google-services") // firebase
    id("kotlin-kapt")
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

android {
    namespace = "com.pill_mate.pill_mate_android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pill_mate.pill_mate_android"
        minSdk = 24
        targetSdk = 35
        versionCode = 10006
        versionName = "1.2.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        resValue("string", "kakao_app_key", "\"${properties["KAKAO_NATIVE_APP_KEY"]}\"")
        resValue("string", "scheme_kakao_app_key", "\"kakao${properties["KAKAO_NATIVE_APP_KEY"]}\"")

        buildConfigField(
            "String", "BASE_URL", properties.getProperty("BASE_URL")
        )
        buildConfigField(
            "String", "KAKAO_NATIVE_APP_KEY", properties.getProperty("KAKAO_NATIVE_APP_KEY")
        )
        buildConfigField(
            "String", "SERVICE_API_KEY", "\"${properties.getProperty("SERVICE_API_KEY")}\""
        )
    }

    signingConfigs {
        create("release") {
            val storeFilePath = properties["storeFile"] as? String
            val storePasswordProp = properties["storePassword"] as? String
            val keyAliasProp = properties["keyAlias"] as? String
            val keyPasswordProp = properties["keyPassword"] as? String

            if (storeFilePath != null && storePasswordProp != null && keyAliasProp != null && keyPasswordProp != null) {
                storeFile = file(storeFilePath)
                storePassword = storePasswordProp
                keyAlias = keyAliasProp
                keyPassword = keyPasswordProp
            }
        }
    }

    buildTypes {
        getByName("debug") { //applicationIdSuffix = ".debug" //debug와 release를 구분해야할 경우
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.v2.user)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx) // 카카오 로그인
    implementation("com.kakao.sdk:v2-user:2.15.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // retrofit2
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") //json
    implementation("com.tickaroo.tikxml:annotation:0.8.13")
    implementation("com.tickaroo.tikxml:core:0.8.13")
    implementation("com.tickaroo.tikxml:retrofit-converter:0.8.13")
    implementation(libs.firebase.messaging.ktx)
    kapt("com.tickaroo.tikxml:processor:0.8.13")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("androidx.recyclerview:recyclerview:1.3.1") // recyclerview
    implementation("com.airbnb.android:lottie-compose:5.2.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.github.bumptech.glide:glide:4.15.1") // Glide
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.security:security-crypto:1.1.0-alpha06") //EncryptedSharedPreferences
    implementation(platform("com.google.firebase:firebase-bom:33.12.0")) // Import the Firebase BoM
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-ads:23.0.0") // AdMob
}
