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

package com.nlab.reminder.domain.feature.home.tag.delete.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nlab.reminder.R
import com.nlab.reminder.core.android.fragment.viewLifecycleScope
import com.nlab.reminder.core.android.view.throttleClicks
import com.nlab.reminder.databinding.FragmentHomeTagDeleteDialogBinding
import com.nlab.reminder.domain.common.android.fragment.popBackStackWithResult
import com.nlab.reminder.domain.feature.home.view.HomeTagDeleteResult
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

/**
 * @author Doohyun
 */
class HomeTagDeleteDialogFragment : BottomSheetDialogFragment() {
    private val args: HomeTagDeleteDialogFragmentArgs by navArgs()

    private val binding: FragmentHomeTagDeleteDialogBinding get() = checkNotNull(_binding)
    private var _binding: FragmentHomeTagDeleteDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentHomeTagDeleteDialogBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.descriptionTextview.apply {
            text = resources.getQuantityString(
                R.plurals.home_tag_delete_text_label,
                args.tagUsageCount.toInt(),
                args.tag.name,
                args.tagUsageCount
            )
        }

        binding.cancelButton
            .throttleClicks()
            .map { HomeTagDeleteResult(args.tag, isConfirmed = false) }
            .onEach { result -> popBackStackWithResult(args.requestKey, result) }
            .launchIn(viewLifecycleScope)

        binding.confirmButton
            .throttleClicks()
            .map { HomeTagDeleteResult(args.tag, isConfirmed = true) }
            .onEach { result -> popBackStackWithResult(args.requestKey, result) }
            .launchIn(viewLifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}