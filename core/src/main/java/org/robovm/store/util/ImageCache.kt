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

import java.io.File
import java.io.IOException
import java.io.InputStream

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils

import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class ImageCache private constructor() {

    var saveLocation: File? = null
        private set

    private val client = OkHttpClient()

    fun getImage(url: String): File? {
        Objects.requireNonNull<File>(saveLocation, "Must specify a save location!")
        Objects.requireNonNull(url, "url")

        val destination = File(saveLocation, FilenameUtils.getName(url))
        if (destination.exists()) {
            return destination
        }
        return null
    }

    fun downloadImage(url: String): File {
        return downloadImage(url, true)!!
    }

    private fun downloadImage(url: String, retryOnFail: Boolean): File? {
        Objects.requireNonNull<File>(saveLocation, "Must specify a save location!")
        Objects.requireNonNull(url, "url")

        val destination = File(saveLocation, FilenameUtils.getName(url))
        if (destination.exists()) {
            return destination
        }

        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            val code = response.code()
            if (code >= 200 && code < 300) {
                // Success
                val `in` = response.body().byteStream()
                FileUtils.copyInputStreamToFile(`in`, destination)
                return destination
            } else if (retryOnFail) {
                // Error
                return downloadImage(PLACEHOLDER_URL, false)
            }
        } catch (e: IOException) {
            System.err.println("file download failed: " + e.message)
            if (retryOnFail) {
                return downloadImage(PLACEHOLDER_URL, false)
            }
        }

        return null
    }

    fun downloadImage(url: String, completion: (File?)->Unit) {
        downloadImage(url, completion, true)
    }

    private fun downloadImage(url: String, completion: (File?)->Unit, retryOnFail: Boolean) {
        Objects.requireNonNull<File>(saveLocation, "Must specify a save location!")
        Objects.requireNonNull(url, "url")
        Objects.requireNonNull(completion, "completion")

        val destination = File(saveLocation, FilenameUtils.getName(url))
        if (destination.exists()) {
            completion(destination)
            return
        }

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            @Throws(IOException::class)
            override fun onResponse(response: Response) {
                val code = response.code()
                if (code >= 200 && code < 300) {
                    // Success
                    val `in` = response.body().byteStream()
                    FileUtils.copyInputStreamToFile(`in`, destination)
                    completion(destination)
                } else if (retryOnFail) {
                    // Error
                    downloadImage(PLACEHOLDER_URL, completion, false)
                } else {
                    completion(null)
                }
            }

            override fun onFailure(request: Request, e: IOException) {
                System.err.println("file download failed: " + e.message)
                if (retryOnFail) {
                    downloadImage(PLACEHOLDER_URL, completion, false)
                } else {
                    completion(null)
                }
            }
        })
    }

    fun setSaveLocation(saveLocation: String) {
        this.saveLocation = File(saveLocation)
    }

    companion object {
        private val PLACEHOLDER_URL = "http://store-app-images.robovm.com/placeholder.jpg"

        val instance = ImageCache()
    }
}
