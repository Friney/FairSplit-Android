buildscript {
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.56.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.9.0")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.10.0" apply false
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
}