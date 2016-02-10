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
package pl.marcinmoskala.store.api

import android.content.Context
import com.bumptech.glide.Glide
import pl.marcinmoskala.store.APP
import pl.marcinmoskala.store.MUG
import pl.marcinmoskala.store.NRISE
import pl.marcinmoskala.store.TSHIRT
import pl.marcinmoskala.store.model.Basket
import pl.marcinmoskala.store.model.Product
import pl.marcinmoskala.store.model.User
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.POST
import rx.Observable

class Rest private constructor() {
    var api: RestAPI = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create<RestAPI>(RestAPI::class.java)

    fun getProducts(completion: (List<Product>)->Unit) =
            completion(listOf(NRISE, MUG, TSHIRT, APP))

    interface RestAPI {
        @POST("auth")
        fun auth(@Body body: AuthRequest): Observable<AuthResponse>

        @GET("products")
        fun products(): Observable<ProductsResponse>

        @POST("order")
        fun order(@Body body: OrderRequest): Observable<APIResponse>
    }

    companion object {
        val instance = Rest()
        private val API_URL = "https://store-app.robovm.com/test/"
    }
}
