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

package com.nlab.reminder.domain.feature.schedule.all

import androidx.paging.PagingData
import com.nlab.reminder.core.effect.SideEffectHandle
import com.nlab.reminder.domain.common.schedule.CompletedScheduleShownRepository
import com.nlab.reminder.domain.common.schedule.ScheduleUiState
import com.nlab.reminder.domain.common.schedule.ModifyScheduleCompleteUseCase
import com.nlab.reminder.domain.common.schedule.genSchedule
import com.nlab.reminder.test.genBoolean
import kotlinx.coroutines.flow.emptyFlow
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * @author Doohyun
 */
fun genAllScheduleSnapshot(
    isCompletedScheduleShown: Boolean = genBoolean(),
    pagingScheduled: PagingData<ScheduleUiState> = PagingData.empty()
): AllScheduleSnapshot = AllScheduleSnapshot(pagingScheduled, isCompletedScheduleShown)

fun genAllScheduleEvents(): Set<AllScheduleEvent> = setOf(
    AllScheduleEvent.Fetch,
    AllScheduleEvent.OnToggleCompletedScheduleShownClicked,
    AllScheduleEvent.OnAllScheduleSnapshotLoaded(genAllScheduleSnapshot()),
    AllScheduleEvent.OnModifyScheduleCompleteClicked(genSchedule().id(), genBoolean())
)

fun genAllScheduleStates(): Set<AllScheduleState> = setOf(
    AllScheduleState.Init,
    AllScheduleState.Loading,
    AllScheduleState.Loaded(genAllScheduleSnapshot())
)

fun genAllScheduleSideEffects(): Set<AllScheduleSideEffect> = setOf(
    AllScheduleSideEffect.ShowErrorPopup
)

fun genAllScheduleStateMachine(
    sideEffectHandle: SideEffectHandle<AllScheduleSideEffect> = mock(),
    getAllScheduleSnapshot: GetAllScheduleSnapshotUseCase = mock { whenever(mock()) doReturn emptyFlow() },
    modifyScheduleComplete: ModifyScheduleCompleteUseCase = mock(),
    completedScheduleShownRepository: CompletedScheduleShownRepository = mock()
) = AllScheduleStateMachine(
    sideEffectHandle,
    getAllScheduleSnapshot,
    modifyScheduleComplete,
    completedScheduleShownRepository
)

fun genAllScheduleEventSample(): AllScheduleEvent = genAllScheduleEvents().first()
fun genAllScheduleStateSample(): AllScheduleState = genAllScheduleStates().first()
fun genAllScheduleSideEffectSample(): AllScheduleSideEffect = genAllScheduleSideEffects().first()