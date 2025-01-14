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

import androidx.recyclerview.widget.DiffUtil
import com.nlab.reminder.domain.common.schedule.ScheduleUiState

/**
 * @author thalys
 */
class ScheduleUiStateDiffCallback : DiffUtil.ItemCallback<ScheduleUiState>() {
    private var isItemsTheSameWhenDrag: Boolean = false

    override fun areItemsTheSame(oldItem: ScheduleUiState, newItem: ScheduleUiState): Boolean =
        if (isItemsTheSameWhenDrag) oldItem.id == newItem.id
        else oldItem.schedule == newItem.schedule

    override fun areContentsTheSame(oldItem: ScheduleUiState, newItem: ScheduleUiState): Boolean =
        oldItem == newItem

    fun setDragMode(isEnable: Boolean) {
        isItemsTheSameWhenDrag = isEnable
    }
}