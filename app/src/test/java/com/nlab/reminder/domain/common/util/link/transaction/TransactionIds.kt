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

package com.nlab.reminder.domain.common.util.link.transaction

import com.nlab.reminder.domain.common.util.transaction.TransactionId
import com.nlab.reminder.domain.common.util.transaction.TransactionIdGenerator
import com.nlab.testkit.genBothify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * @author Doohyun
 */
fun genTransactionIdGenerator(expected: String = genBothify()): TransactionIdGenerator = mock {
    whenever(mock.generate()) doReturn genTransactionId(expected)
}

fun genTransactionId(expected: String = genBothify()): TransactionId = TransactionId(expected)