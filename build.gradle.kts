// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    // Make sure that you have the Google services Gradle plugin 4.4.1+ dependency
    id("com.google.gms.google-services") version "4.4.2" apply false

    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}