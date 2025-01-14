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

package com.nlab.reminder.internal.common.di

import com.nlab.reminder.core.kotlin.coroutine.util.Delay
import com.nlab.reminder.domain.common.util.transaction.TransactionIdGenerator
import com.nlab.reminder.domain.common.schedule.*
import com.nlab.reminder.domain.common.schedule.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * @author Doohyun
 */
@Module
@InstallIn(ViewModelComponent::class)
class ScheduleModule {
    @ViewModelScoped
    @Provides
    fun provideCompleteMarkRepository(): CompleteMarkRepository = DefaultCompleteMarkRepository()

    @ViewModelScoped
    @Provides
    fun provideSelectionRepository(): SelectionRepository = DefaultSelectionRepository()

    @ViewModelScoped
    @Provides
    fun provideSelectionModeRepository(): SelectionModeRepository = DefaultSelectionModeRepository()

    @Provides
    fun provideScheduleUiStateFlowFactory(
        completeMarkRepository: CompleteMarkRepository,
        selectionRepository: SelectionRepository,
    ): ScheduleUiStateFlowFactory = DefaultScheduleUiStateFlowFactory(
        completeMarkRepository,
        selectionRepository
    )

    @Provides
    fun provideUpdateCompleteUseCase(
        transactionIdGenerator: TransactionIdGenerator,
        scheduleRepository: ScheduleRepository,
        completeMarkRepository: CompleteMarkRepository
    ): UpdateCompleteUseCase = DefaultUpdateCompleteUseCase(
        transactionIdGenerator,
        scheduleRepository,
        completeMarkRepository,
        delayUntilTransactionPeriod = Delay(timeMillis = 500),
    )

    @Provides
    fun provideBulkUpdateCompleteUseCase(scheduleRepository: ScheduleRepository): BulkUpdateCompleteUseCase =
        DefaultBulkUpdateCompleteUseCase(scheduleRepository)
}