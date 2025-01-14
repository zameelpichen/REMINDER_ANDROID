/*
 * Copyright (C) 2023 The N's lab Open Source Project
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

package com.nlab.reminder.convention

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * @author Doohyun
 */
internal fun Project.configureAndroidJacoco(extension: AndroidComponentsExtension<*, *, *>) {
    val jacocoTestReport = tasks.create("jacocoTestReport")

    extension.onVariants { variant ->
        val unitTestCapitalized = variant.unitTestCapitalized() ?: return@onVariants
        val unitTestTaskName: String = variant.unitTestTaskName() ?: return@onVariants
        val reportTask = tasks.register("jacoco${unitTestCapitalized}Report", JacocoReport::class) {
            dependsOn(unitTestTaskName)
            reports {
                xml.required.set(true)
                html.required.set(true)
            }

            sourceDirectories.setFrom(androidJacocoSourcesDirectories(variant))
            classDirectories.setFrom(androidJacocoClassDirectories(variant))
            executionData.setFrom(file("$buildDir/jacoco/$unitTestTaskName.exec"))
        }

        jacocoTestReport.dependsOn(reportTask)

        tasks.withType<Test>().configureEach {
            configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = false

                // Required for JDK 11 with the above
                // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
                excludes = listOf("jdk.internal.*")
            }
        }
    }
}

private fun Project.androidJacocoSourcesDirectories(variant: Variant): ConfigurableFileCollection =
    files(
        "$projectDir/src/main/java",
        "$projectDir/src/main/kotlin",
        "$projectDir/src/${variant.name}/java",
        "$projectDir/src/${variant.name}/kotlin"
    )

private fun Project.androidJacocoClassDirectories(variant: Variant): ConfigurableFileTree =
    fileTree("$buildDir/tmp/kotlin-classes/${variant.name}") {
        exclude(
            jacocoExcludePatterns + setOf(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/android/**",
                "**/view/**",
                "**/test/**",
                "**/di/**",
                "**/*_PublicEventsKt.class",    /* filtering PublicEvent generated classes */
                "**/*Args*.*",                  /* filtering Navigation Component generated classes */
                "**/*Directions*.*"             /* filtering Navigation Component generated classes */
            )
        )
    }
