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
package org.robovm.store.api

import org.robovm.store.model.Basket
import org.robovm.store.model.Product
import org.robovm.store.model.User
import org.robovm.store.util.ImageCache
import org.robovm.store.util.Objects.requireNonNull
import retrofit.*
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.POST
import java.util.*

class RoboVMWebService private constructor() {

    var api: RoboVMAPI? = null
        private set

    private var authToken: AuthToken? = null
    var currentUser: User? = null
        private set
    private var products: List<Product>? = null
    val basket = Basket()

    fun setup(test: Boolean = false): RoboVMWebService {
        // Create a REST adapter which points to the RoboVM API.
        val retrofit = Retrofit.Builder().baseUrl(if (test) API_TEST_URL else API_URL).addConverterFactory(GsonConverterFactory.create()).build()

        // Create an instance of our RoboVM API interface.
        api = retrofit.create<RoboVMAPI>(RoboVMAPI::class.java)

        return this
    }

    fun authenticate(username: String, password: String, completion: (Boolean?) -> Unit) {
        requireNonNull(username, "username")
        requireNonNull(password, "password")
        requireNonNull(completion, "completion")

        if (isAuthenticated) {
            if (currentUser == null) {
                currentUser = User()
            }
            ActionWrapper.WRAPPER.invoke(completion, true)
        } else {
            api!!.auth(AuthRequest(username, password)).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(response: Response<AuthResponse>, retrofit: Retrofit) {
                    var success = false
                    if (response.isSuccess) {
                        val body = response.body()
                        if (body.success) {
                            success = true
                            authToken = AuthToken(body.authToken, Runnable { // Token timed out.
                                // TODO token timed out
                            })
                        }
                    }
                    if (success) {
                        currentUser = User()
                    }
                    ActionWrapper.WRAPPER.invoke(completion, success)
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                    ActionWrapper.WRAPPER.invoke(completion, false)
                }
            })
        }
    }

    fun getProducts(completion: (List<Product>?)->Unit) {
        requireNonNull(completion)

        if (products != null) {
            ActionWrapper.WRAPPER.invoke(completion, products)
        } else {
            api!!.products().enqueue(object : Callback<ProductsResponse> {
                override fun onResponse(response: Response<ProductsResponse>, retrofit: Retrofit) {
                    var products: List<Product>? = null
                    if (response.isSuccess) {
                        val body = response.body()
                        if (body.success) {
                            products = body.products
                        }
                    }

                    this@RoboVMWebService.products = products
                    if (products == null) {
                        products = ArrayList<Product>()
                    }
                    ActionWrapper.WRAPPER.invoke(completion, products)
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()

                    ActionWrapper.WRAPPER.invoke(completion, ArrayList<Product>())
                }
            })
        }
    }

    fun placeOrder(user: User, completion: (APIResponse?)->Unit) {
        requireNonNull(user, "user")
        requireNonNull(completion)

        api!!.order(OrderRequest(authToken!!, currentUser!!, basket)).enqueue(object : Callback<APIResponse> {
            override fun onResponse(response: Response<APIResponse>, retrofit: Retrofit) {
                if (response.isSuccess) {
                    ActionWrapper.WRAPPER.invoke(completion, response.body())
                } else {
                    ActionWrapper.WRAPPER.invoke(completion, null)
                }
            }

            override fun onFailure(t: Throwable) {
                t.printStackTrace()
                ActionWrapper.WRAPPER.invoke(completion, null)
            }
        })
    }

    fun preloadProductImages() {
        if (products != null) {
            Thread {
                for (product in products!!) {
                    for (url in product.imageUrls!!) {
                        ImageCache.instance.downloadImage(url)
                    }
                }
            }.start()
        }
    }

    val isAuthenticated: Boolean
        get() = authToken != null && authToken!!.isExpired()

    interface RoboVMAPI {
        @POST("auth")
        fun auth(@Body body: AuthRequest): Call<AuthResponse>

        @GET("products")
        fun products(): Call<ProductsResponse>

        @POST("order")
        fun order(@Body body: OrderRequest): Call<APIResponse>
    }

    abstract class ActionWrapper {

        abstract operator fun <T> invoke(action: (T?)->Unit, result: T?)

        companion object {
            var WRAPPER: ActionWrapper = object : ActionWrapper() {
                override operator fun <T> invoke(action: (T?)->Unit, result: T?) {
                    action.invoke(result)
                }
            }
        }
    }

    companion object {
        val instance = RoboVMWebService()

        private val API_URL = "https://store-app.robovm.com/api/"
        private val API_TEST_URL = "https://store-app.robovm.com/test/"
    }
}
