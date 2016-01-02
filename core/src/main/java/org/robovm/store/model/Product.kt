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
package org.robovm.store.model

import java.text.NumberFormat
import java.util.ArrayList
import java.util.Random

class Product {
    val id: String = ""
    val name: String = ""
    val description: String = ""
    val price: Double = 0.toDouble()
    val type: ProductType = ProductType.TShirt
    val colors: List<ProductColor> = listOf()
    val sizes: List<ProductSize> = listOf()

    private val random = Random()
    private var imageIndex = -1

    val priceDescription: String
        get() = if (price < 0.01) "Free" else NumberFormat.getCurrencyInstance().format(price)

    val imageUrls: List<String>?
        get() {
            val urls = ArrayList<String>()
            if (colors != null) {
                for (color in colors) {
                    urls.addAll(color.imageUrls)
                }
            }
            return urls
        }

    val imageUrl: String
        get() {
            val imageUrls = imageUrls
            if (imageUrls == null || imageUrls.isEmpty()) {
                return ""
            }
            if (imageUrls.size == 1) {
                return imageUrls[0]
            }
            if (imageIndex == -1) {
                imageIndex = random.nextInt(imageUrls.size)
            }
            return imageUrls[imageIndex]
        }

    override fun toString(): String {
        return name
    }
}
