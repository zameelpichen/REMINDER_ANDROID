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

package com.nlab.testkit

import com.github.javafaker.Faker

/**
 * @author Doohyun
 */

private val f = Faker()

/**
 * Create a string using the length of [numericString].
 * @param numericString String that contains only'#' character.
 * @return random number string.
 */
fun genNumerify(numericString: String = "###"): String = f.numerify(numericString)

/**
 * Create a string using the length of [letterString].
 * @param letterString String that contains only'?' character.
 * @param isUpper If true, the result string is upper case.
 * @return random alphabet string.
 */
fun genLetterify(letterString: String = "???", isUpper: Boolean = false): String = f.letterify(letterString, isUpper)

/**
 * Create a string using the length of [string].
 * @param string String that contains only '#' or '?' character. '#' is number, '?' is alphabet.
 * @param isUpper If true, the result string is upper case.
 * @return random string.
 */
fun genBothify(string: String = "???###", isUpper: Boolean = false): String = f.bothify(string, isUpper)

/**
 * Create a number using the length of [numericString].
 * @param numericString String that contains only '#' character.
 * @return random long type number.
 */
fun genLong(numericString: String = "#######"): Long = genNumerify(numericString).toLong()

/**
 * Create a number using the length of [numericString].
 * @param numericString String that contains only '#' character.
 * @return random int type number.
 */
fun genInt(numericString: String = "####"): Int = genNumerify(numericString).toInt()

/**
 * Create a boolean value.
 * @return random boolean value.
 */
fun genBoolean(): Boolean = genInt("#") % 2 == 0