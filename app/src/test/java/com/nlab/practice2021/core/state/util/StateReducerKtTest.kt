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

package com.nlab.practice2021.core.state.util

import com.nlab.practice2021.core.state.TestState
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.hamcrest.MatcherAssert.assertThat

/**
 * @author Doohyun
 */
internal class StateReducerKtTest {
    @Test
    fun `ignore update when started value was changed`() {
        val initState = TestState.State2()
        val updateState = TestState.StateInit()
        val state: MutableStateFlow<TestState> = MutableStateFlow(initState)
        val reduceState = StateReducer<TestState>()

        state.value = updateState
        reduceState(
            state,
            curState = initState,
            newState = TestState.State1()
        )
        assertThat(state.value, equalTo(updateState))
    }

    @Test
    fun `emit update when started value was not changed`() {
        val initState = TestState.State2()
        val changeState = TestState.State1()
        val state: MutableStateFlow<TestState> = MutableStateFlow(initState)
        val reduceState = StateReducer<TestState>()
        reduceState(
            state,
            curState = initState,
            newState = changeState
        )
        assertThat(state.value, equalTo(changeState))
    }
}