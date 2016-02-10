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

package pl.marcinmoskala.store.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Drawable
import pl.marcinmoskala.store.util.Colors

class BadgeDrawable(private val child: Drawable) : Drawable() {
    private val badgePaint: Paint
    private val textPaint: Paint
    private val badgeBounds = RectF()
    private val txtBounds = Rect()
    var count = 0
        set(count) {
            field = count
            invalidateSelf()
        }
    private var myAlpha = 255

    private var alphaAnimator: ValueAnimator? = null

    init {
        badgePaint = Paint()
        badgePaint.isAntiAlias = true
        badgePaint.color = Colors.White

        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.color = Colors.Green
        textPaint.textSize = 16f
        textPaint.textAlign = Paint.Align.CENTER
    }

    fun setCountAnimated(newCount: Int) {
        if (alphaAnimator != null) {
            alphaAnimator!!.cancel()
            alphaAnimator = null
        }
        val duration = 300

        alphaAnimator = ObjectAnimator.ofInt(this, "alpha", 255, 0)
        alphaAnimator!!.setDuration(duration.toLong())
        alphaAnimator!!.repeatMode = ValueAnimator.REVERSE
        alphaAnimator!!.repeatCount = 1
        alphaAnimator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                animation.removeAllListeners()
                count = newCount
            }
        })
        alphaAnimator!!.start()
    }

    override fun draw(canvas: Canvas) {
        child.draw(canvas)
        if (count <= 0) {
            return
        }
        badgePaint.alpha = myAlpha
        textPaint.alpha = myAlpha
        badgeBounds.set(0f, 0f, (bounds.width() / 2).toFloat(), (bounds.height() / 2).toFloat())
        canvas.drawRoundRect(badgeBounds, 8f, 8f, badgePaint)
        textPaint.textSize = 8 * badgeBounds.height() / 10
        val text = count.toString()
        textPaint.getTextBounds(text, 0, text.length, txtBounds)
        canvas.drawText(text, badgeBounds.centerX(),
                badgeBounds.bottom - (badgeBounds.height() - txtBounds.height()) / 2 - 1f, textPaint)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        child.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    override fun getIntrinsicWidth(): Int {
        return child.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return child.intrinsicHeight
    }

    override fun setAlpha(alpha: Int) {
        this.myAlpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        child.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return child.opacity
    }
}
