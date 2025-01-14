/*
 * Copyright (C) 2022 The N's lab Open Source Project
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
package com.nlab.reminder.domain.feature.home.view

import android.os.Parcelable
import com.nlab.reminder.core.android.navigation.Navigation
import com.nlab.reminder.core.android.navigation.NavigationController
import com.nlab.reminder.domain.common.tag.Tag
import kotlinx.parcelize.Parcelize

/**
 * @author Doohyun
 */
data class HomeTagConfigNavigation(val requestKey: String, val tag: Tag) : Navigation
data class HomeTagDeleteNavigation(val requestKey: String, val tag: Tag, val usageCount: Long) : Navigation
data class HomeTagRenameNavigation(val requestKey: String, val tag: Tag, val usageCount: Long) : Navigation

@Parcelize
data class HomeTagConfigResult(
    val tag: Tag,
    val isRenameRequested: Boolean,
    val isDeleteRequested: Boolean
) : Parcelable

@Parcelize
data class HomeTagRenameResult(
    val tag: Tag,
    val rename: String,
    val isConfirmed: Boolean
) : Parcelable

@Parcelize
data class HomeTagDeleteResult(
    val tag: Tag,
    val isConfirmed: Boolean
) : Parcelable

fun NavigationController.navigateToTagConfig(requestKey: String, tag: Tag) =
    navigateTo(HomeTagConfigNavigation(requestKey, tag))

fun NavigationController.navigateToTagDelete(requestKey: String, tag: Tag, usageCount: Long) =
    navigateTo(HomeTagDeleteNavigation(requestKey, tag, usageCount))

fun NavigationController.navigateToTagRename(requestKey: String, tag: Tag, usageCount: Long) =
    navigateTo(HomeTagRenameNavigation(requestKey, tag, usageCount))
