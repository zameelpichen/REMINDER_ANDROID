/*
 * Copyright (C) 2022 The N"s lab Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// FIXME Annotations must be added before Gradle 8.1.
// FIXME https://developer.android.com/studio/build/migrate-to-catalogs?hl=ko#migrate-plugins
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("nlab.android.application")
    id("nlab.android.application.jacoco")
    id("kotlin-parcelize")
    kotlin("kapt")
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.android.navigation.safearges)
}

android {
    namespace = "com.nlab.reminder"

    defaultConfig {
        applicationId = namespace
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kapt {
            arguments {
                arg("dagger.fastInit", "enabled")
                arg("dagger.formatGeneratedSource", "disabled")
                arg("room.schemaLocation", "$buildDir/schemas")
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug") // TODO make release key..
        }
    }

    buildTypes.forEach { buildType ->
        if (buildType.isMinifyEnabled) {
            buildType.proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            buildType.proguardFile("proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("release") {
            java.srcDirs("src/release/java")
            res.srcDirs("src/release/res")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":state:core"))
    kapt(project(":state:compiler"))

    implementation(libs.kotlin.coroutines.android)

    implementation(libs.android.core.ktx)
    implementation(libs.android.appcompat)
    implementation(libs.android.constaintlayout)
    implementation(libs.android.recyclerview)
    implementation(libs.android.lifecycle.viewmodel.ktx)
    implementation(libs.android.lifecycle.runtime.ktx)
    implementation(libs.android.fragment.ktx)
    implementation(libs.android.navigation.fragment.ktx)
    implementation(libs.android.navigation.ui.ktx)
    implementation(libs.android.room.runtime)
    implementation(libs.android.room.ktx)
    kapt(libs.android.room.compiler)
    implementation(libs.android.startup.runtime)
    implementation(libs.android.datastore.preferences)

    implementation(libs.google.android.material)
    implementation(libs.google.flexbox)
    implementation(libs.google.hilt.android)
    kapt(libs.google.hilt.android.compiler)

    implementation(libs.timber)

    implementation(libs.glide)
    kapt(libs.glide.compiler)

    implementation(libs.jsoup)

    implementation(libs.afollested.dragselectRecyclerView)

    debugImplementation(libs.facebook.flipper)
    debugImplementation(libs.facebook.flipper.leakcanary)
    debugImplementation(libs.facebook.soloader)

    debugImplementation(libs.squeare.leakcanary)

    testImplementation(project(":testkit"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.coroutines.test)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.javafaker)
    androidTestImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.javafaker)
    androidTestImplementation(libs.android.test.junit)
    androidTestImplementation(libs.android.test.espresso.core)
    androidTestImplementation(libs.android.test.runner)
    androidTestImplementation(libs.android.test.rules)
}

kapt {
    // guide in dagger hilt
    // https://developer.android.com/training/dependency-injection/hilt-android?hl=ko#setup
    correctErrorTypes = true
}
