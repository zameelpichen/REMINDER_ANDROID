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

package com.nlab.reminder.domain.common.schedule.impl

import com.nlab.reminder.domain.common.schedule.*
import kotlinx.coroutines.flow.*

/**
 * @author Doohyun
 */
class DefaultScheduleUiStateFlowFactory(
    private val completeMarkRepository: CompleteMarkRepository
) : ScheduleUiStateFlowFactory {
    override fun combineWith(scheduleFlow: Flow<List<Schedule>>): Flow<List<ScheduleUiState>> =
        combine(
            scheduleFlow,
            completeMarkRepository.get()
                .onStart { emit(emptyMap()) }
                .distinctUntilChanged(),
            transform = ::transformScheduleUiStatesBy
        )

    companion object {
        private fun transformScheduleUiStatesBy(
            schedules: List<Schedule>,
            completeMarkSnapshot: Map<ScheduleId, CompleteMark>
        ): List<ScheduleUiState> = schedules.map { schedule ->
            ScheduleUiState(
                schedule,
                isCompleteMarked = completeMarkSnapshot[schedule.id()]?.isComplete ?: schedule.isComplete
            )
        }
    }
}