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

import android.graphics.*
import android.graphics.drawable.Drawable

class CircleDrawable(private val bmp: Bitmap) : Drawable() {
    private val bmpShader: BitmapShader
    private val paint: Paint
    private val oval: RectF

    init {
        bmpShader = BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint = Paint()
        paint.isAntiAlias = true
        paint.setShader(bmpShader)
        oval = RectF()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawOval(oval, paint)
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        oval.set(0f, 0f, bounds.width().toFloat(), bounds.height().toFloat())
    }

    override fun getIntrinsicWidth(): Int {
        return bmp.width
    }

    override fun getIntrinsicHeight(): Int {
        return bmp.height
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }
}
