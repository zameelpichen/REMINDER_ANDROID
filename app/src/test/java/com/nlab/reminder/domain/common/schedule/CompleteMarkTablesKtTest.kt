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

package com.nlab.reminder.domain.common.schedule

import com.nlab.testkit.genBoolean
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

/**
 * @author thalys
 */
class CompleteMarkTablesKtTest {
    @Test
    fun `get tables info when table has value`() {
        val expectedCompleteMarked: Boolean = genBoolean()
        val schedule: Schedule = genSchedule()
        val completeMarkTable: CompleteMarkTable =
            mapOf(schedule.id to genCompleteMark(isComplete = expectedCompleteMarked))

        assertThat(completeMarkTable.isCompleteMarked(schedule), equalTo(expectedCompleteMarked))
    }

    @Test
    fun `created scheduleUiState with owners complete when snapshot was empty`() {
        val schedule: Schedule = genSchedule()
        val completeMarkTable: CompleteMarkTable = emptyMap()

        assertThat(completeMarkTable.isCompleteMarked(schedule), equalTo(schedule.isComplete))
    }
}