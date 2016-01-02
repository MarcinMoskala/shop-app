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

package org.robovm.store.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

object Images {
    private val bmpCache = ConcurrentHashMap<String, Bitmap>()
    private var SCREEN_WIDTH = 320f

    fun setImageFromUrlAsync(imageView: ImageView, url: String) {
        fromUrl(url, { imageView.setImageBitmap(it) })
    }

    fun setImageFromUrlAsync(imageView: BitmapHolder, url: String) {
        fromUrl(url, { imageView.setImageBitmap(it) })
    }

    fun setImageFromUrlAsync(imageView: ImageView, url: String, completion: Runnable) {
        fromUrl(url, { bitmap ->
            imageView.setImageBitmap(bitmap)
            completion.run()
        })
    }

    fun setImageFromUrlAsync(imageView: BitmapHolder, url: String, completion: Runnable) {
        fromUrl(url, { bitmap ->
            imageView.setImageBitmap(bitmap)
            completion.run()
        })
    }

    fun fromUrl(url: String): Bitmap {
        var bmp: Bitmap? = bmpCache[url]
        if (bmp == null) {
            val image = ImageCache.instance.downloadImage(url)
            bmp = saveBitmap(url, image)
        }
        return bmp
    }

    fun fromUrl(url: String, completion: (Bitmap) -> Unit) {
        val bmp = bmpCache[url]
        if (bmp != null) {
            completion.invoke(bmp)
            return
        }

        val image = ImageCache.instance.getImage(url)
        if (image != null) {
            completion.invoke(saveBitmap(url, image))
        } else {
            ImageCache.instance.downloadImage(url) { i -> completion.invoke(saveBitmap(url, i!!)) }
        }
    }

    private fun saveBitmap(url: String, imagePath: File): Bitmap {
        val bmp = BitmapFactory.decodeFile(imagePath.absolutePath)
        if (bmp != null) {
            bmpCache.put(url, bmp)
        }
        return bmp
    }

    fun setScreenWidth(screenWidth: Float) {
        SCREEN_WIDTH = screenWidth
    }
}
