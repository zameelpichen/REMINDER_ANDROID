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

package com.nlab.reminder.core.state.impl

import com.nlab.reminder.core.state.Action
import com.nlab.reminder.core.state.ActionProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author Doohyun
 */
internal class DefaultActionProcessor<A : Action>(
    private val scope: CoroutineScope,
    private val onActionReceived: suspend (A) -> Unit,
) : ActionProcessor<A> {
    override fun send(action: A): Job = scope.launch { onActionReceived(action) }
}