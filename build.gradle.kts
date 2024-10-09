// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()

    }
    dependencies {

        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}





plugins {

    id("com.android.application") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    //kotlin("kapt") version "1.9.24" apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false

}