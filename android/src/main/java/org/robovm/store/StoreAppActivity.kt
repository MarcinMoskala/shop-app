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

package org.robovm.store

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MenuItem
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.fragments.*
import org.robovm.store.model.Product
import org.robovm.store.util.ImageCache
import org.robovm.store.util.Images

class StoreAppActivity : Activity() {
    private var baseFragment: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        Images.setScreenWidth(metrics.widthPixels.toFloat())
        ImageCache.instance.setSaveLocation(cacheDir.absolutePath)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        // Retain fragments so don't set home if state is stored.
        if (fragmentManager.backStackEntryCount == 0) {
            val productFragment = ProductListFragment({ product, itemVerticalOffset ->
                val productDetails = ProductDetailsFragment(product, itemVerticalOffset)
                FragmentSwitch().switchScreens(fragmentManager, productDetails)
            })
            baseFragment = productFragment.id
            FragmentSwitch().switchScreens(fragmentManager, productFragment, false, true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("baseFragment", baseFragment)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        baseFragment = savedInstanceState.getInt("baseFragment")
    }

    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart_menu_item -> {
                FragmentSwitch().switchScreens(fragmentManager, BasketFragment())
                return true
            }
            android.R.id.home -> {
                // pop full backstack when going home.
                fragmentManager.popBackStack(baseFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                setupActionBar()
                return true
            }
        }

        return super.onMenuItemSelected(featureId, item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setupActionBar(fragmentManager.backStackEntryCount != 0)
    }

    fun setupActionBar(showUp: Boolean = false) {
        actionBar!!.setDisplayHomeAsUpEnabled(showUp)
    }
}
