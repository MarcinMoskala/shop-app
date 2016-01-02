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
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import org.robovm.store.R

class SlidingLayout : LinearLayout {

    private var primaryView: View? = null
    private var secondaryView: View? = null
    var initialMainViewDelta: Int = 0

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
        orientation = LinearLayout.VERTICAL
    }

    fun getPrimaryView(): View {
        if (primaryView == null) primaryView = findViewById(PRIMARY_VIEW_ID)
        return primaryView!!
    }

    fun getSecondaryView(): View {
        if (secondaryView == null) secondaryView = findViewById(SECONDARY_VIEW_ID)
        return secondaryView!!
    }

    // Inverts children drawing order so that our main item (top) is drawn last
    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return childCount - 1 - i
    }

    override fun getTranslationY(): Float {
        return getPrimaryView().translationY / initialMainViewDelta
    }

    override fun setTranslationY(translationY: Float) {
        getPrimaryView().translationY = translationY * initialMainViewDelta
    }

    override fun getAlpha(): Float {
        return getSecondaryView().alpha
    }

    override fun setAlpha(alpha: Float) {
        getSecondaryView().alpha = alpha
    }

    override fun setTranslationX(translationX: Float) {
        val power = Math.pow(translationX.toDouble(), 5.0).toFloat()
        super.setTranslationX(power * (width / 2))
        super.setTranslationY(-1f * power * (height / 2).toFloat())
        super.setAlpha(1 - translationX)
        super.setScaleX(1 - .8f * translationX)
        super.setScaleY(1 - .8f * translationX)
    }

    companion object {
        private val PRIMARY_VIEW_ID = R.id.productImage
        private val SECONDARY_VIEW_ID = R.id.descriptionLayout
    }
}
