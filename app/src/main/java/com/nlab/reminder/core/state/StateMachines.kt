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

import com.nlab.reminder.core.util.test.annotation.ExcludeFromGeneratedTestReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus

/**
 * @author thalys
 */
@ExcludeFromGeneratedTestReport
@Suppress("FunctionName")
inline fun <E : Event, S : State> StateMachine(
    block: StateMachine<E, S>.() -> Unit
): StateMachine<E, S> = StateMachine<E, S>().apply(block)

fun <E : Event, S : State> StateMachine<E, S>.asContainer(
    scope: CoroutineScope,
    initState: S,
    config: StateMachineConfig = StateMachineConfig()
): StateContainer<E, S> {
    val stateFlow = MutableStateFlow(initState)
    return StateContainerImpl(
        ConfigurableEventProcessor(stateMachine = this, scope, stateFlow, config),
        stateFlow.stateIn(scope, config.sharingStarted, initState)
    )
}

fun <E : Event, S : State> StateMachine<E, S>.asContainer(
    scope: CoroutineScope,
    initState: S,
    fetchEvent: E,
    config: StateMachineConfig = StateMachineConfig()
): StateContainer<E, S> {
    val stateFlow = MutableStateFlow(initState)
    val eventProcessor: EventProcessor<E> = ConfigurableEventProcessor(stateMachine = this, scope, stateFlow, config)
    return StateContainerImpl(
        eventProcessor,
        stateFlow
            .onStart(onStartToFetchConverter(eventProcessor, fetchEvent))
            .stateIn(scope, config.sharingStarted, initState)
    )
}

@Suppress("FunctionName")
private fun <E : Event, S : State> ConfigurableEventProcessor(
    stateMachine: StateMachine<E, S>,
    scope: CoroutineScope,
    stateFlow: MutableStateFlow<S>,
    config: StateMachineConfig
): EventProcessor<E> = StateMachineEventProcessor(
    state = stateFlow,
    scope = if (config.dispatcher == null) scope else scope + config.dispatcher,
    stateMachine = stateMachine.apply {
        val exceptionHandler = config.exceptionHandler ?: return@apply
        catch { exceptionHandler(it) }
    }
)

// Jacoco could not measure coverage in onStart suspend function..
private fun <E : Event, S : State> onStartToFetchConverter(
    eventProcessor: EventProcessor<E>,
    fetchEvent: E
): FlowCollector<S>.() -> Unit = { eventProcessor.send(fetchEvent) }