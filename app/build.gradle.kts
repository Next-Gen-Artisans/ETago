plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.nextgenartisans.etago"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nextgenartisans.etago"
        minSdk = 26
        targetSdk = 33
        versionCode = 2
        versionName = "1.1"
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

}

configurations.all {
    resolutionStrategy {
        // Prefer org.jetbrains:annotations over com.intellij:annotations
        force("org.jetbrains:annotations:13.0")
    }
    // Exclude com.intellij:annotations globally, if necessary
    exclude(group = "com.intellij", module = "annotations")
}


dependencies {
    // about us feedback form
    implementation ("me.zhanghai.android.materialratingbar:library:1.4.0")

    // Tensorflow Lite dependencies for Google Play services
    implementation("com.google.android.gms:play-services-tflite-java:16.0.1")
    // Optional: include Tensorflow Lite Support Library
    implementation("com.google.android.gms:play-services-tflite-support:16.0.1")
    //To use the GPU delegate with the Task APIs:
    implementation("com.google.android.gms:play-services-tflite-gpu:16.1.0")
    implementation("org.tensorflow:tensorflow-lite-task-vision-play-services:0.4.4")
    implementation("org.tensorflow:tensorflow-lite:2.15.0")
    implementation("org.tensorflow:tensorflow-lite-task-vision:0.4.4")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("androidx.navigation:navigation-fragment:2.7.5")
    implementation("androidx.navigation:navigation-ui:2.7.5")

    //Implment FastAPIEndPoint
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))

    //Cloud Storage
    implementation("com.google.firebase:firebase-storage")

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependency for the Google Play services library and specify its version
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-database:20.3.0")

    //Facebook login implementation
    implementation("com.facebook.android:facebook-login:latest.release")

    //ShapeableImageView
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.room:room-compiler:2.6.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //CAMERA IMPLEMENTATION
    // CameraX core library using the camera2 implementation
    val camerax_version = "1.4.0-alpha02"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${camerax_version}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${camerax_version}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${camerax_version}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${camerax_version}")




}

