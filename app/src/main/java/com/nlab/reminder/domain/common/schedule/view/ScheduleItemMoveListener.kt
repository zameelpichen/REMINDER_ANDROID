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

package com.nlab.reminder.domain.common.schedule.view

import com.nlab.reminder.core.android.recyclerview.ItemMoveListener
import com.nlab.reminder.domain.common.schedule.ScheduleUiState

/**
 * @author thalys
 */
class ScheduleItemMoveListener(
    private val getItem: (position: Int) -> ScheduleUiState?,
    private val notifyItemMoved: (fromPosition: Int, toPosition: Int) -> Unit
) : ItemMoveListener {
    override fun onMove(fromPosition: Int, toPosition: Int): Boolean {
        val fromState: ScheduleUiState? = getItem(fromPosition)
        val toState: ScheduleUiState? = getItem(toPosition)
        val isMoveNeeded: Boolean =
            fromState != null && toState != null && fromState.isComplete == toState.isComplete
        if (isMoveNeeded) {
            notifyItemMoved(fromPosition, toPosition)
        }
        return isMoveNeeded
    }
}