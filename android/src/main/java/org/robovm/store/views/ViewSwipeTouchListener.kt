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

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

import java.util.ArrayList

class ViewSwipeTouchListener(context: Context, private val subviewID: Int) : GestureDetector.SimpleOnGestureListener(), View.OnTouchListener {
    private val detector: GestureDetector
    private var targetView: View? = null
    private val config: ViewConfiguration

    private val listeners = ArrayList<EventListener>()

    init {
        this.detector = GestureDetector(context, this)
        this.config = ViewConfiguration.get(context)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (targetView == null) {
            targetView = if (subviewID == 0) v else v.findViewById(subviewID)
        }
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            for (listener in listeners) {
                listener.onSwipeGestureEnd()
            }
            //            boolean dismiss = event.getAction() != MotionEvent.ACTION_CANCEL &&
            //                    targetView.getTranslationX() > targetView.getWidth() / 2;
            snapView(false)
        }
        detector.onTouchEvent(event)
        return true
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        // We are only interested in an horizontal right-side fling
        if (velocityY > velocityX || velocityX < 0) {
            return super.onFling(e1, e2, velocityX, velocityY)
        }
        snapView(true)
        return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        var distanceX = distanceX
        for (listener in listeners) {
            listener.onSwipeGestureBegin()
        }
        distanceX = -distanceX
        if (Math.abs(distanceY) > Math.abs(distanceX) + config.scaledTouchSlop || distanceX < 0 && targetView!!.translationX <= 0) {
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
        targetView!!.translationX = Math.max(0f, targetView!!.translationX + distanceX)
        targetView!!.alpha = (targetView!!.width - targetView!!.translationX) / targetView!!.width.toFloat()
        return true
    }

    fun resetSwipe() {
        if (targetView != null) {
            targetView!!.alpha = 1f
            targetView!!.translationX = 0f
        }
    }

    private fun snapView(dismiss: Boolean) {
        if (targetView == null) {
            return
        }

        val targetAlpha = if (dismiss) 0 else 1
        val targetTranslation = if (dismiss) targetView!!.width else 0
        val a = ObjectAnimator.ofPropertyValuesHolder(targetView,
                PropertyValuesHolder.ofFloat("alpha", targetView!!.alpha, targetAlpha.toFloat()),
                PropertyValuesHolder.ofFloat("translationX", targetView!!.translationX, targetTranslation.toFloat()))

        if (dismiss) {
            a.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                }

                override fun onAnimationEnd(animation: Animator) {
                    animation.removeAllListeners()

                    for (listener in listeners) {
                        listener.onItemSwipped()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {
                }

                override fun onAnimationRepeat(animation: Animator) {
                }
            })
        }
        a.start()
    }

    fun addEventListener(listener: EventListener) {
        listeners.add(listener)
    }

    fun removeEventListener(listener: EventListener) {
        listeners.remove(listener)
    }

    interface EventListener {
        fun onSwipeGestureBegin()

        fun onSwipeGestureEnd()

        fun onItemSwipped()
    }
}
