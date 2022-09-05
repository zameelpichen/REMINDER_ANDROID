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

package com.nlab.reminder.domain.feature.home.di

import androidx.lifecycle.SavedStateHandle
import com.nlab.reminder.domain.common.android.lifecycle.tag
import com.nlab.reminder.domain.feature.home.tag.rename.HomeTagRenameStateMachineFactory
import com.nlab.reminder.domain.feature.home.tag.rename.view.HomeTagRenameInitText
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

/**
 * @author Doohyun
 */
@Module
@InstallIn(ViewModelComponent::class)
class HomeTagRenameViewModelModule {
    @Provides
    fun provideInitText(savedStateHandle: SavedStateHandle): HomeTagRenameInitText {
        return HomeTagRenameInitText(savedStateHandle.tag.name)
    }

    @Provides
    fun provideHomeTagRenameStateMachineFactory(): HomeTagRenameStateMachineFactory {
        return HomeTagRenameStateMachineFactory()
    }
}