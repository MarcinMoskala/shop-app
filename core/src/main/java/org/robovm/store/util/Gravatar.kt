package org.robovm.store.util

import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

import com.squareup.okhttp.Callback
import org.robovm.store.api.RoboVMWebService.ActionWrapper

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class Gravatar private constructor() {

    private val client = OkHttpClient()

    @Throws(UnsupportedEncodingException::class)
    fun getUrl(email: String, size: Int, rating: Rating): String {
        if (size < 1 || size > 600) {
            throw IllegalArgumentException("The image size should be between 1 and 600")
        }
        return "%s%s&s=%d&r=%s&d=%s".format(URL, "", size, rating.name.toLowerCase(), URLEncoder.encode(DEFAULT_IMAGE_URL, "UTF-8"))
    }

    fun getImageBytes(email: String, size: Int, rating: Rating, completion: (ByteArray?)->Unit) {
        Objects.requireNonNull(completion, "completion")

        try {
            val request = Request.Builder().url(getUrl(email, size, rating)).build()
            client.newCall(request).enqueue(object : Callback {
                @Throws(IOException::class)
                override fun onResponse(response: Response) {
                    val code = response.code()
                    if (code >= 200 && code < 300) {
                        // Success
                        val bytes = response.body().bytes()
                        ActionWrapper.WRAPPER.invoke(completion, bytes)
                    } else {
                        ActionWrapper.WRAPPER.invoke(completion, null)
                    }
                }

                override fun onFailure(request: Request, e: IOException) {
                    e.printStackTrace()
                    ActionWrapper.WRAPPER.invoke(completion, null)
                }
            })
        } catch (e: IOException) {
            e.printStackTrace()
            ActionWrapper.WRAPPER.invoke(completion, null)
        }

    }

    enum class Rating {
        G, PG, R, X
    }

    companion object {
        private val URL = "http://www.gravatar.com/avatar.php?gravatar_id="
        private val DEFAULT_IMAGE_URL = "https://raw.githubusercontent.com/robovm/robovm-store-app/master/gravatar-default.png"
        val instance = Gravatar()
    }
}
