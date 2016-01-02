/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * 
 */
package org.robovm.store.util

class Color(var hex: Int) {
    var r: Double = 0.toDouble()
    var g: Double = 0.toDouble()
    var b: Double = 0.toDouble()

    init {
        r = ((hex and 16711680 shr 16) / 255f).toDouble()
        g = ((hex and 65280 shr 8) / 255f).toDouble()
        b = ((hex and 255) / 255f).toDouble()
    }

    companion object {
        val Purple = Color(11818422)
        val Blue = Color(44783)
        val DarkBlue = Color(2899536)
        val Green = Color(9684516)
        val Gray = Color(4473924)
        val LightGray = Color(6710886)
    }
}
