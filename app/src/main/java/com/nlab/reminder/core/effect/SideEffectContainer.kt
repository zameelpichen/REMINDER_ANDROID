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

package com.nlab.reminder.core.effect

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext

/**
 * @author thalys
 */
class SideEffectContainer<T : SideEffect>(
    private val channel: Channel<T> = Channel(Channel.BUFFERED),
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : SideEffectHandle<T> {
    val sideEffectFlow: Flow<T> = channel.receiveAsFlow()
    override suspend fun post(sideEffect: T) = withContext(dispatcher) {
        channel.send(sideEffect)
    }
}