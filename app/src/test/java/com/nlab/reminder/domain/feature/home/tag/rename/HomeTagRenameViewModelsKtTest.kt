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

import com.nlab.reminder.core.state.StateController
import com.nlab.reminder.core.state.verifyStateSendExtension
import com.nlab.reminder.test.genBothify
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * @author Doohyun
 */
class HomeTagRenameViewModelsKtTest {
    @Test
    fun testExtensions() {
        val stateController: StateController<HomeTagRenameEvent, HomeTagRenameState> = mock()
        val randomString: String = genBothify()
        val viewModel = HomeTagRenameViewModel(
            stateControllerFactory = mock {
                whenever(mock.create(any(), any())) doReturn stateController
            }
        )

        verifyStateSendExtension(
            stateController,
            HomeTagRenameEvent.OnRenameTextInput(randomString)
        ) { viewModel.onRenameTextInput(randomString) }

        verifyStateSendExtension(
            stateController,
            HomeTagRenameEvent.OnRenameTextClearClicked
        ) { viewModel.onRenameTextClearClicked() }

        verifyStateSendExtension(
            stateController,
            HomeTagRenameEvent.OnKeyboardShownWhenViewCreated
        ) { viewModel.onKeyboardShownWhenViewCreated() }

        verifyStateSendExtension(
            stateController,
            HomeTagRenameEvent.OnCancelClicked
        ) { viewModel.onCancelClicked() }

        verifyStateSendExtension(
            stateController,
            HomeTagRenameEvent.OnConfirmClicked
        ) { viewModel.onConfirmClicked() }
    }
}