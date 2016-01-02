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
import org.robovm.store.api.RoboVMWebService.ActionWrapper
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

        RoboVMWebService.instance.setup()
        ActionWrapper.WRAPPER = object : ActionWrapper() {
            override fun <T> invoke(action: (T?)->Unit, result: T?) {
                runOnUiThread { action.invoke(result) }
            }
        }

        setContentView(R.layout.main)

        // Retain fragments so don't set home if state is stored.
        if (fragmentManager.backStackEntryCount == 0) {
            val productFragment = ProductListFragment({ product, itemVerticalOffset -> this.showProductDetail(product, itemVerticalOffset) })
            baseFragment = productFragment.id
            switchScreens(productFragment, false, true)
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
                showBasket()
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

    fun switchScreens(fragment: Fragment, animated: Boolean = true, isRoot: Boolean = false): Int {
        val transaction = fragmentManager.beginTransaction()

        if (animated) {
            transaction.setCustomAnimations(getInAnimationForFragment(fragment), getOutAnimationForFragment(fragment))
        }
        transaction.replace(R.id.contentArea, fragment)
        if (!isRoot) {
            transaction.addToBackStack(null)
        }

        setupActionBar(!isRoot)

        return transaction.commit()
    }

    private fun getInAnimationForFragment(fragment: Fragment): Int {
        var animIn = R.anim.enter

        when (fragment) {
            is ProductDetailsFragment -> animIn = R.anim.product_detail_in
            is BasketFragment -> animIn = R.anim.basket_in
        }
        return animIn
    }

    private fun getOutAnimationForFragment(fragment: Fragment): Int {
        var animOut = R.anim.exit

        when (fragment) {
            is ProductDetailsFragment -> animOut = R.anim.product_detail_out
            is BasketFragment -> {}
        }
        return animOut
    }

    fun showProductDetail(product: Product, itemVerticalOffset: Int) {
        val productDetails = ProductDetailsFragment(product, itemVerticalOffset)
        productDetails.setAddToBasketListener { order ->
            RoboVMWebService.instance.basket.add(order)
            setupActionBar()
        }
        switchScreens(productDetails)
    }

    fun setupActionBar(showUp: Boolean = false) {
        actionBar!!.setDisplayHomeAsUpEnabled(showUp)
    }

    fun showBasket() {
        val basket = BasketFragment()
        basket.setCheckoutListener( Runnable{ this.showLogin() })
        switchScreens(basket)
    }

    fun showLogin() {
        val login = LoginFragment()
        login.setLoginSuccessListener(Runnable { this.showAddress() })
        switchScreens(login)
    }

    fun showAddress() {
        val shipping = ShippingDetailsFragment(RoboVMWebService.instance.currentUser!!)
        shipping.setOrderPlacedListener( Runnable { this.orderCompleted() })
        switchScreens(shipping)
    }

    fun orderCompleted() {
        fragmentManager.popBackStack(baseFragment, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        setupActionBar()

        switchScreens(BragFragment(), true, true)
    }
}
