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
 */

package org.robovm.store.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import org.robovm.store.R

class SwipableListItem : FrameLayout {

    private val mainContent: View? = null
    private val secondaryContent: View? = null
    var swipeListener: ViewSwipeTouchListener? = null
        private set

    private var shadow: Paint? = null

    constructor(context: Context) : super(context) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        initialize()
    }

    private fun initialize() {
        shadow = Paint()
        shadow!!.isAntiAlias = true
        swipeListener = ViewSwipeTouchListener(context, R.id.swipeContent)
        setOnTouchListener(swipeListener)
        swipeListener!!.addEventListener(object : ViewSwipeTouchListener.EventListener {
            override fun onSwipeGestureBegin() {
            }

            override fun onSwipeGestureEnd() {
            }

            override fun onItemSwipped() {
                swipeListener!!.resetSwipe()
            }
        })
    }

    override fun dispatchDraw(canvas: Canvas) {
        // Draw interior shadow
        canvas.save()
        canvas.clipRect(0, 0, width, height)
        canvas.drawPaint(shadow)
        canvas.restore()

        super.dispatchDraw(canvas)

        // Draw custom list separator
        canvas.save()
        canvas.clipRect(0, height - 2, width, height)
        canvas.drawColor(Color.rgb(LIGHT_TONE, LIGHT_TONE, LIGHT_TONE))
        canvas.restore()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        shadow!!.setShader(LinearGradient(0f, 0f, 0f, (bottom - top).toFloat(), COLORS, POSITIONS, Shader.TileMode.REPEAT))
    }

    companion object {
        private val DARK_TONE = 211
        private val LIGHT_TONE = 221

        private val COLORS = intArrayOf(Color.rgb(DARK_TONE, DARK_TONE, DARK_TONE), Color.rgb(LIGHT_TONE, LIGHT_TONE, LIGHT_TONE), Color.rgb(LIGHT_TONE, LIGHT_TONE, LIGHT_TONE), Color.rgb(DARK_TONE, DARK_TONE, DARK_TONE))
        private val POSITIONS = floatArrayOf(0f, .15f, .85f, 1f)
    }
}
