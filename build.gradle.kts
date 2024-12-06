// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    id ("com.android.library") version "7.2.2" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.1")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21")
        classpath("com.google.gms:google-services:4.3.15")
    }
}

