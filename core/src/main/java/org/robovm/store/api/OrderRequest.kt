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
import org.robovm.store.model.Order
import org.robovm.store.model.User

class OrderRequest(authTokenObject: AuthToken, user: User, basket: Basket) {
    val authToken = authTokenObject.tokenString
    val firstName = user.firstName
    val lastName = user.lastName
    val address1 = user.address1
    val address2 = user.address2
    val zipCode = user.zipCode
    val city = user.city
    val state = user.state
    val phone = user.phone
    val country = user.country
    val products = basket.orders
}
