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

package com.nlab.reminder.domain.common.schedule.view

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.bumptech.glide.Glide
import com.nlab.reminder.R
import com.nlab.reminder.core.android.content.getThemeColor
import com.nlab.reminder.core.android.lifecycle.event
import com.nlab.reminder.core.android.lifecycle.filterLifecycleEvent
import com.nlab.reminder.core.android.recyclerview.absoluteAdapterOptionalPosition
import com.nlab.reminder.core.android.recyclerview.bindingAdapterOptionalPosition
import com.nlab.reminder.core.android.transition.transitionListenerOf
import com.nlab.reminder.core.android.view.initWithLifecycleOwner
import com.nlab.reminder.core.android.view.throttleClicks
import com.nlab.reminder.core.android.view.touches
import com.nlab.reminder.databinding.ViewItemScheduleBinding
import com.nlab.reminder.domain.common.schedule.ScheduleId
import com.nlab.reminder.domain.common.schedule.ScheduleUiState
import kotlinx.coroutines.flow.*

/**
 * @author thalys
 */
class ScheduleUiStateViewHolder(
    private val binding: ViewItemScheduleBinding,
    selectionEnabled: Flow<Boolean>,
    onCompleteClicked: (Int) -> Unit,
    onDeleteClicked: (Int) -> Unit,
    onLinkClicked: (Int) -> Unit,
    onSelectTouched: (Int, Boolean) -> Unit,
    onDragHandleClicked: (RecyclerView.ViewHolder) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private val linkThumbnailPlaceHolderDrawable: Drawable? = with(itemView) {
        AppCompatResources.getDrawable(context, R.drawable.ic_schedule_link_error)
            ?.let(DrawableCompat::wrap)
            ?.apply { DrawableCompat.setTint(mutate(), context.getThemeColor(R.attr.tint_schedule_placeholder)) }
    }
    private val layoutContentNormalSet: ConstraintSet = ConstraintSet().apply { clone(binding.layoutContent) }
    private val layoutContentSelectionSet =
        ConstraintSet().apply { load(itemView.context, R.layout.view_item_schedule_layout_content_selection) }

    private var curUiState: ScheduleUiState? = null

    init {
        binding.initWithLifecycleOwner { lifecycleOwner ->
            buttonComplete
                .throttleClicks()
                .onEach { onCompleteClicked(bindingAdapterOptionalPosition ?: return@onEach) }
                .launchIn(lifecycleOwner.lifecycleScope)

            layoutDelete
                .throttleClicks()
                .onEach { onDeleteClicked(bindingAdapterOptionalPosition ?: return@onEach) }
                .launchIn(lifecycleOwner.lifecycleScope)

            cardLink
                .throttleClicks()
                .onEach { onLinkClicked(bindingAdapterOptionalPosition ?: return@onEach) }
                .launchIn(lifecycleOwner.lifecycleScope)

            buttonDragHandle
                .touches()
                .filter { event -> event.action == MotionEvent.ACTION_DOWN }
                .onEach { onDragHandleClicked(this@ScheduleUiStateViewHolder) }
                .launchIn(lifecycleOwner.lifecycleScope)

            buttonSelection
                .touches()
                .filter { event -> event.action == MotionEvent.ACTION_DOWN }
                .onEach {
                    onSelectTouched(
                        absoluteAdapterOptionalPosition ?: return@onEach,
                        buttonSelection.isSelected
                    )
                }
                .launchIn(lifecycleOwner.lifecycleScope)

            lifecycleOwner.lifecycle.event()
                .filterLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                .onEach {
                    // clear selection animation.
                    TransitionManager.endTransitions(layoutContent)
                }
                .launchIn(lifecycleOwner.lifecycleScope)

            selectionEnabled
                .flowWithLifecycle(lifecycleOwner.lifecycle)
                .onEach { isSelectionMode -> buttonSelection.isEnabled = isSelectionMode }
                .onEach { isSelectionMode -> buttonComplete.isEnabled = isSelectionMode.not() }
                .onEach { isSelectionMode ->
                    TransitionManager.beginDelayedTransition(
                        layoutContent,
                        AutoTransition().apply {
                            duration = 300
                            addListener(transitionListenerOf(
                                onStart = { layoutDelete.visibility = View.INVISIBLE },
                                onEnd = { layoutDelete.visibility = View.VISIBLE },
                                onCancel = { layoutDelete.visibility = View.VISIBLE }
                            ))
                        }
                    )
                    val applyConstraintSet: ConstraintSet =
                        if (isSelectionMode) layoutContentSelectionSet else layoutContentNormalSet
                    applyConstraintSet.applyTo(layoutContent)
                }
                .launchIn(lifecycleOwner.lifecycleScope)
        }
    }

    fun onBind(scheduleUiState: ScheduleUiState) {
        curUiState = scheduleUiState

        binding.textviewTitle.text = scheduleUiState.title
        binding.textviewNote.text = scheduleUiState.note
        binding.cardLink.visibility = if (scheduleUiState.isLinkCardVisible) View.VISIBLE else View.GONE
        binding.textviewLink.text = scheduleUiState.link
        binding.buttonComplete
            .apply { isSelected = scheduleUiState.isCompleteMarked }
            .apply {
                contentDescription = context.getString(
                    if (scheduleUiState.isCompleteMarked) R.string.schedule_complete_checkbox_undo_contentDescription
                    else R.string.schedule_complete_checkbox_contentDescription,
                    scheduleUiState.title
                )
            }
        binding.buttonSelection
            .apply { isSelected = scheduleUiState.isSelected }
            .apply {
                contentDescription = context.getString(
                    if (scheduleUiState.isSelected) R.string.schedule_selection_checkbox_undo_contentDescription
                    else R.string.schedule_selection_checkbox_contentDescription,
                    scheduleUiState.title
                )
            }
        binding.textviewTitleLink
            .apply { visibility = if (scheduleUiState.linkMetadata.isTitleVisible) View.VISIBLE else View.GONE }
            .apply { text = scheduleUiState.linkMetadata.title }
        binding.imageviewBgLinkThumbnail
            .apply { visibility = if (scheduleUiState.linkMetadata.isImageVisible) View.VISIBLE else View.GONE }
            .let { view ->
                Glide.with(view.context)
                    .load(scheduleUiState.linkMetadata.imageUrl)
                    .override(1000, 400)
                    .dontTransform()
                    .optionalCenterCrop()
                    .placeholder(linkThumbnailPlaceHolderDrawable)
                    .error(linkThumbnailPlaceHolderDrawable)
                    .into(view)
            }
    }

    fun isSelected(): Boolean {
        return binding.buttonSelection.isSelected
    }

    fun bindingScheduleId(): ScheduleId? {
        return curUiState?.id
    }

    companion object {
        fun of(
            parent: ViewGroup,
            selectionEnabled: Flow<Boolean>,
            onCompleteClicked: (position: Int) -> Unit,
            onDeleteClicked: (position: Int) -> Unit,
            onLinkClicked: (position: Int) -> Unit,
            onSelectTouched: (Int, Boolean) -> Unit,
            onDragHandleClicked: (RecyclerView.ViewHolder) -> Unit
        ): ScheduleUiStateViewHolder = ScheduleUiStateViewHolder(
            ViewItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            selectionEnabled,
            onCompleteClicked,
            onDeleteClicked,
            onLinkClicked,
            onSelectTouched,
            onDragHandleClicked
        )
    }
}