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

package com.nlab.reminder.internal.common.tag.impl

import com.nlab.reminder.core.kotlin.util.Result
import com.nlab.reminder.domain.common.tag.Tag
import com.nlab.reminder.domain.common.tag.TagRepository
import com.nlab.reminder.domain.common.tag.genTag
import com.nlab.reminder.domain.common.tag.genTags
import com.nlab.reminder.internal.common.android.database.ScheduleTagListDao
import com.nlab.reminder.internal.common.android.database.TagDao
import com.nlab.reminder.internal.common.android.database.TagEntity
import com.nlab.reminder.internal.common.android.database.toEntity
import com.nlab.reminder.internal.common.database.toEntities
import com.nlab.reminder.test.*
import com.nlab.testkit.genBothify
import com.nlab.testkit.genLong
import com.nlab.testkit.once
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.Test
import org.mockito.kotlin.*

/**
 * @author Doohyun
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LocalTagRepositoryTest {
    private fun genTagRepository(
        tagDao: TagDao = mock(),
        scheduleTagListDao: ScheduleTagListDao = mock()
    ): TagRepository = LocalTagRepository(tagDao, scheduleTagListDao)

    @Test
    fun `tagDao found when get`() {
        val tagDao: TagDao = mock()
        genTagRepository(tagDao = tagDao).get()
        verify(tagDao, once()).find()
    }

    @Test
    fun `notify tag list when dao updated`() = runTest {
        val executeDispatcher = genFlowExecutionDispatcher(testScheduler)
        val actualTags = mutableListOf<List<Tag>>()
        val firstTags: List<Tag> = listOf(genTag())
        val secondTags: List<Tag> = genTags().sortedBy { it.name }.reversed()
        val tagDao: TagDao = mock {
            val mockFlow = flow {
                emit(firstTags.toEntities())

                delay(500)
                emit(secondTags.toEntities())
            }
            whenever(mock.find()) doReturn mockFlow.flowOn(executeDispatcher)
        }

        genTagRepository(tagDao = tagDao)
            .get()
            .onEach(actualTags::add)
            .launchIn(genFlowObserveCoroutineScope())

        advanceTimeBy(1_000)
        assertThat(actualTags, equalTo(listOf(firstTags, secondTags)))
    }

    @Test
    fun `repository get usage count from scheduleTagListDao`() = runTest {
        val usageCount: Long = genLong()
        val input: Tag = genTag()
        val scheduleTagListDao: ScheduleTagListDao = mock {
            whenever(mock.findTagUsageCount(input.tagId)) doReturn usageCount
        }
        val result = genTagRepository(scheduleTagListDao = scheduleTagListDao).getUsageCount(input)

        assertThat(result, equalTo(Result.Success(usageCount)))
    }

    @Test
    fun `repository return error when scheduleTagListDao occurred error while find by usage count`() = runTest {
        val exception = RuntimeException()
        val input: Tag = genTag()
        val scheduleTagListDao: ScheduleTagListDao = mock { whenever(mock.findTagUsageCount(any())) doThrow exception }
        val result = genTagRepository(scheduleTagListDao = scheduleTagListDao).getUsageCount(input)

        assertThat(result, equalTo(Result.Failure(exception)))
    }

    @Test
    fun `tagDao updated when repository update name requested`() = runTest {
        val name: String = genBothify()
        val input: Tag = genTag()
        val tagDao: TagDao = mock()

        genTagRepository(tagDao = tagDao).updateName(input, name)
        verify(tagDao, once()).update(TagEntity(input.tagId, name))
    }

    @Test
    fun `tagDao return error when repository update name requested`() = runTest {
        val exception = RuntimeException()
        val tagDao: TagDao = mock { whenever(mock.update(any())) doThrow exception }
        val result = genTagRepository(tagDao = tagDao).updateName(genTag(), genBothify())

        assertThat(result, equalTo(Result.Failure(exception)))
    }

    @Test
    fun `tagDao delete tag when repository deleting requested`() = runTest {
        val input: Tag = genTag()
        val tagDao: TagDao = mock()

        genTagRepository(tagDao = tagDao).delete(input)
        verify(tagDao, once()).delete(input.toEntity())
    }

    @Test
    fun `tagDao return error when repository deleting requested`() = runTest {
        val exception = RuntimeException()
        val input: Tag = genTag()
        val tagDao: TagDao = mock { whenever(mock.delete(any())) doThrow exception }
        val result = genTagRepository(tagDao = tagDao).delete(input)

        assertThat(result, equalTo(Result.Failure(exception)))
    }
}