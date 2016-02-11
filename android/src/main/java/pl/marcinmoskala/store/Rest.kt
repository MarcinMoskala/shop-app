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
package pl.marcinmoskala.store

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.logging.HttpLoggingInterceptor
import com.squareup.okhttp.logging.HttpLoggingInterceptor.Level.BODY
import pl.marcinmoskala.store.model.Product
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import retrofit.http.GET
import rx.Observable

class Rest private constructor() {
    val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
            .apply { setLevel(BODY) };
    val client: OkHttpClient = OkHttpClient()
            .apply { interceptors().add(interceptor) }

    var api: RestAPI = Retrofit.Builder()
            .baseUrl(API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
            .create<RestAPI>(RestAPI::class.java)

    interface RestAPI {
        @GET("products")
        fun getProducts(): Observable<List<Product>>
    }

    companion object {
        val instance = Rest()
        private val API_URL = "http://nootro.pl/api/"
    }
}
