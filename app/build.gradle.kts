import org.jetbrains.kotlin.cli.jvm.main

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // 适用于 Kotlin 项目

    // Add the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")

}
configurations {
    create("cleanedAnnotations")
    implementation {
        exclude(group = "com.intellij", module = "annotations")
    }
}


android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystore/key.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }
        create("release") {
            storeFile = file("../keystore/key.jks")
            storePassword = "123456"
            keyAlias = "key0"
            keyPassword = "123456"
        }

    }
    namespace = "com.android.nfc.system"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.nfc.system"
        minSdk = 26
        targetSdk = 34
        versionCode = 108
        versionName = "1.0.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters.addAll(listOf("arm64-v8a"))
        }

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    applicationVariants.all {
        outputs.all {
            val outputFileName = "LocationAssistant-${versionName}-${buildType.name}.apk"
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                outputFileName
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
        compose = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        pickFirst("lib/*/libc++_shared.so")
    }
}



dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.7.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.exoplayer:extension-okhttp:2.19.1")

    //baidu map
    implementation("com.baidu.lbsyun:BaiduMapSDK_Map:7.6.2")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Search:7.6.2")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Location_All:9.6.4")
    implementation("com.baidu.lbsyun:BaiduMapSDK_Util:7.6.2")

    //Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    //nav
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.tencent.bugly:crashreport:latest.release")
    implementation("com.tencent.shiply:upgrade:2.2.0")
    implementation("com.tencent.shiply:upgrade-ui:2.2.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //Xposed
    implementation("com.github.kyuubiran:EzXHelper:2.2.0")
    compileOnly("de.robv.android.xposed:api:82")

}
    kapt {
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}