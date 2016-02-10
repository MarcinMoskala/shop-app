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

package pl.marcinmoskala.store.presentation.basket

import android.app.ListFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.api.Rest
import pl.marcinmoskala.store.basket
import pl.marcinmoskala.store.model.Basket
import pl.marcinmoskala.store.presentation.login.LoginFragment
import pl.marcinmoskala.store.presentation.shipping.ShippingDetailsFragment
import pl.marcinmoskala.store.util.switchScreen

class BasketFragment : ListFragment() {
    private var checkoutButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.basket, container, false).apply {
            checkoutButton = (findViewById(R.id.checkoutBtn) as Button).apply {
                setOnClickListener {
                    switchScreen(ShippingDetailsFragment())
                }
            }

            pivotY = 0f
            pivotX = container!!.width.toFloat()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.dividerHeight = 0
        listView.divider = null
        listAdapter = GroceryListAdapter(view.context)
        if (listAdapter.isEmpty) checkoutButton!!.visibility = INVISIBLE
        basket.addOnBasketChangeListener {
            checkoutButton!!.visibility = if (basket.size() > 0) VISIBLE else INVISIBLE
        }
    }
}
