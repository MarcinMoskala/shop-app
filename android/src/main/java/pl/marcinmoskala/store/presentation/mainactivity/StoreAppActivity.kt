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

package pl.marcinmoskala.store.presentation.mainactivity

import android.app.Activity
import android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.api.Rest
import pl.marcinmoskala.store.model.Product
import pl.marcinmoskala.store.presentation.basket.BasketFragment
import pl.marcinmoskala.store.presentation.product.ProductDetailsFragment
import pl.marcinmoskala.store.presentation.productlist.ProductListFragment
import pl.marcinmoskala.store.util.switchScreen

class StoreAppActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        actionBar!!.setDisplayHomeAsUpEnabled(true)

        if (fragmentManager.backStackEntryCount == 0)
            switchScreen(ProductListFragment(), false, true)
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart_menu_item ->
                switchScreen(BasketFragment())
            android.R.id.home ->
                fragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)
            else -> return super.onMenuItemSelected(featureId, item)
        }
        return true
    }
}