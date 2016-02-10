package pl.marcinmoskala.store.util

import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder.encode

class Gravatar private constructor() {
    fun getUrl(email: String, size: Int, rating: Rating): String =
            "%s%s&s=%d&r=%s&d=%s".format(URL, "", size, rating.name.toLowerCase(), encode(DEFAULT_IMAGE_URL, "UTF-8"))

    @Throws(UnsupportedEncodingException::class)
    fun getImageBytes(email: String, size: Int, rating: Rating, completion: (ByteArray)->Unit) {
        if (size !in 1..600)
            throw IllegalArgumentException("The image size should be between 1 and 600")

        val request = Request.Builder().url(getUrl(email, size, rating)).build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onResponse(response: Response) {
                if (response.code() in 200..299) {
                    completion(response.body().bytes())
                }
            }

            override fun onFailure(request: Request, e: IOException) {
                e.printStackTrace()
            }
        })
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
