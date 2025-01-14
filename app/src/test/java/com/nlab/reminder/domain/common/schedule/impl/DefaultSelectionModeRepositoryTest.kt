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

import com.nlab.testkit.genBoolean
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

/**
 * @author thalys
 */
@ExperimentalCoroutinesApi
class DefaultSelectionModeRepositoryTest {
    @Test
    fun `set default initValue false when not set on constructor`() {
        val selectionModeRepository = DefaultSelectionModeRepository()
        assertThat(selectionModeRepository.enabledStream().value, equalTo(false))
    }

    @Test
    fun `set initValue with constructor parameter`() {
        val initValue: Boolean = genBoolean()
        val selectionModeRepository = DefaultSelectionModeRepository(initValue)

        assertThat(selectionModeRepository.enabledStream().value, equalTo(initValue))
    }

    @Test
    fun `notify new selection mode when repository received setEnabled event`() = runTest {
        val initValue: Boolean = genBoolean()
        val input: Boolean = initValue.not()
        val selectionModeRepository = DefaultSelectionModeRepository(initValue)
        val newValueDeferred = async {
            selectionModeRepository
                .enabledStream()
                .drop(1)
                .take(1)
                .first()
        }

        launch { selectionModeRepository.setEnabled(input) }

        assertThat(newValueDeferred.await(), equalTo(input))
    }
}