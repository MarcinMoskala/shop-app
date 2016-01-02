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

class Order {
    @Transient var product: Product? = null
        private set(product) {
            field = product
            this.id = product!!.id
        }
    @Transient var productSize: ProductSize? = null
    @Transient var productColor: ProductColor? = null

    var id: String = ""
    var size: String = ""
    var color: String = ""

    constructor(order: Order) {
        product = order.product
        setSize(order.getSize())
        setColor(order.getColor())
    }

    constructor(product: Product) {
        this.product = product
        setSize(product.sizes[0])
        setColor(product.colors[0])
    }

    constructor(product: Product, size: ProductSize, color: ProductColor) {
        this.product = product
        setSize(size)
        setColor(color)
    }

    fun getColor(): ProductColor {
        return productColor!!
    }

    fun getSize(): ProductSize {
        return productSize!!
    }

    fun setSize(size: ProductSize) {
        this.productSize = size
        this.size = size.id
    }

    fun setColor(color: ProductColor) {
        this.productColor = color
        this.color = color.name
    }

    override fun toString(): String {
        return product!!.toString() + " " + color + " " + size
    }
}
