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

import android.graphics.*
import android.graphics.drawable.Drawable
import org.robovm.store.util.BitmapHolder

class KenBurnsDrawable(private val defaultColor: Int) : Drawable(), BitmapHolder {
    private var myAlpha: Int = 0
    private var matrix: Matrix? = null
    private val paint: Paint
    private var secondSlot: Boolean = false

    var firstBitmap: Bitmap? = null
        set(firstBitmap) {
            field = firstBitmap
            shader1 = null
            invalidateSelf()
        }
    var secondBitmap: Bitmap? = null
        set(secondBitmap) {
            field = secondBitmap
            shader2 = null
            invalidateSelf()
        }
    private var shader1: BitmapShader? = null
    private var shader2: BitmapShader? = null

    init {
        paint = Paint()
        paint.isAntiAlias = false
        paint.isFilterBitmap = false
    }

    override fun setImageBitmap(bmp: Bitmap) {
        if (secondSlot) {
            secondBitmap = bmp
        } else {
            firstBitmap = bmp
        }
        secondSlot = !secondSlot
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds

        if (myAlpha != 255) {
            paint.alpha = 255
            if (secondBitmap != null) {
                if (shader1 == null) {
                    shader1 = BitmapShader(firstBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
                shader1!!.setLocalMatrix(matrix)
                paint.setShader(shader1)
                canvas.drawRect(bounds, paint)
            } else {
                canvas.drawColor(defaultColor)
            }
        }
        if (myAlpha != 0) {
            paint.alpha = myAlpha
            if (firstBitmap != null) {
                if (shader2 == null) {
                    shader2 = BitmapShader(secondBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                }
                shader2!!.setLocalMatrix(matrix)
                paint.setShader(shader2)
                canvas.drawRect(bounds, paint)
            } else {
                canvas.drawColor(defaultColor)
            }
        }
    }

    fun setMatrix(matrix: Matrix) {
        this.matrix = matrix
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        this.myAlpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}
