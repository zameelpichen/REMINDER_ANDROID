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

package com.nlab.reminder.domain.feature.home.tag.rename

import com.nlab.reminder.core.state.util.controlIn
import com.nlab.reminder.test.genBothify
import com.nlab.reminder.test.genLetterify
import com.nlab.reminder.test.once
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * @author Doohyun
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeTagRenameStateMachineKtTest {
    private fun genEvents(): Set<HomeTagRenameEvent> = setOf(
        HomeTagRenameEvent.OnConfirmClicked,
        HomeTagRenameEvent.OnCancelClicked,
        HomeTagRenameEvent.OnRenameTextInput(text = genBothify())
    )

    private fun genStateMachine(
        homeTagRenameSideEffect: SendHomeTagRenameSideEffect = mock()
    ): HomeTagRenameStateMachine = HomeTagRenameStateMachine(homeTagRenameSideEffect)

    @Test
    fun `update currentText state when OnRenameTextInput sent`() = runTest {
        val changeText = genBothify(string = "????####")
        val initState: HomeTagRenameState = genHomeTagRenameState(currentText = genLetterify("???"))
        val stateController =
            genStateMachine().controlIn(CoroutineScope(Dispatchers.Default), initState)
        stateController
            .send(HomeTagRenameEvent.OnRenameTextInput(changeText))
            .join()
        assertThat(
            stateController.state.value,
            equalTo(initState.copy(currentText = changeText))
        )
    }

    @Test
    fun `clear currentText state when OnRenameTextClearClicked sent`() = runTest {
        val initState: HomeTagRenameState = genHomeTagRenameState(currentText = genLetterify("?"))
        val stateController =
            genStateMachine().controlIn(CoroutineScope(Dispatchers.Default), initState)
        stateController
            .send(HomeTagRenameEvent.OnRenameTextClearClicked)
            .join()
        assertThat(
            stateController.state.value,
            equalTo(initState.copy(currentText = ""))
        )
    }

    @Test
    fun `update disable keyboard shown onViewCreated when OnKeyboardShownWhenViewCreated sent`() = runTest {
        val initState: HomeTagRenameState = genHomeTagRenameState(isKeyboardShowWhenViewCreated = true)
        val stateController =
            genStateMachine().controlIn(CoroutineScope(Dispatchers.Default), initState)
        stateController
            .send(HomeTagRenameEvent.OnKeyboardShownWhenViewCreated)
            .join()
        assertThat(
            stateController.state.value,
            equalTo(initState.copy(isKeyboardShowWhenViewCreated = false))
        )
    }

    @Test
    fun `never updated when any event excluded OnRenameTextInput, OnRenameTextClearClicked, OnKeyboardShownWhenViewCreated sent`() = runTest {
        val initState: HomeTagRenameState = genHomeTagRenameState()
        val stateController =
            genStateMachine().controlIn(CoroutineScope(Dispatchers.Default), initState)
        assertThat(

        genEvents()
            .asSequence()
            .filterNot { it is HomeTagRenameEvent.OnRenameTextInput }
            .filterNot { it is HomeTagRenameEvent.OnRenameTextClearClicked }
            .filterNot { it is HomeTagRenameEvent.OnKeyboardShownWhenViewCreated }
            .map { event ->
                async {
                    stateController.send(event).join()
                    stateController.state.value == initState
                }
            }
            .toList()
            .all { it.await() },
            equalTo(true)
        )
    }

    @Test
    fun `notify complete when confirm clicked`() = runTest {
        val changeText = genBothify()
        val sideEffect: SendHomeTagRenameSideEffect = mock()
        val stateController = genStateMachine(sideEffect)
            .controlIn(CoroutineScope(Dispatchers.Default), genHomeTagRenameState(currentText = changeText))

        stateController
            .send(HomeTagRenameEvent.OnConfirmClicked)
            .join()
        verify(sideEffect, once()).send(HomeTagRenameSideEffectMessage.Complete(changeText))
    }

    @Test
    fun `notify dismiss when cancel clicked`() = runTest {
        val sideEffect: SendHomeTagRenameSideEffect = mock()
        val stateMachine = genStateMachine(sideEffect)
            .controlIn(CoroutineScope(Dispatchers.Default), genHomeTagRenameState())

        stateMachine
            .send(HomeTagRenameEvent.OnCancelClicked)
            .join()
        verify(sideEffect, once()).send(HomeTagRenameSideEffectMessage.Dismiss)
    }
}