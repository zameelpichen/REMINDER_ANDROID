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
package com.nlab.reminder.core.state

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @author thalys
 */
@StateMachineDsl
class StateMachineHandleScope<E : Event> internal constructor(
    private val subscriptionCount: StateFlow<Int>,
    private val eventProcessor: EventProcessor<E>
) : EventProcessor<E> by eventProcessor {
    suspend fun <T> Flow<T>.collectWhileSubscribed(flowCollector: FlowCollector<T>) {
        coroutineScope {
            launch(start = CoroutineStart.UNDISPATCHED) {
                subscriptionCount
                    .map { it > 0 }
                    .distinctUntilChanged()
                    .collectLatest { isActive -> if (isActive) collect(flowCollector) }
            }
        }
    }
}