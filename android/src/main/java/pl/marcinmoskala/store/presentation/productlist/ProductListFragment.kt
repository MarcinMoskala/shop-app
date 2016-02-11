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

package pl.marcinmoskala.store.presentation.productlist

import android.app.ListFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.Rest
import pl.marcinmoskala.store.basket
import pl.marcinmoskala.store.presentation.product.ProductDetailsFragment
import pl.marcinmoskala.store.util.switchScreen
import pl.marcinmoskala.store.util.toast
import pl.marcinmoskala.store.views.BadgeDrawable
import rx.android.schedulers.AndroidSchedulers
import rx.android.schedulers.AndroidSchedulers.mainThread

import rx.schedulers.Schedulers.io
class ProductListFragment : ListFragment() {
    private var basketBadge: BadgeDrawable? = null
    private var badgeCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
        = inflater.inflate(R.layout.robovm_list_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.setDrawSelectorOnTop(true)
        listView.selector = ColorDrawable(Color.parseColor("#30ffffff"))

        if(listAdapter == null){
            listAdapter = ProductListAdapter(view.context)
            Rest.instance.api.getProducts()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribe({
                    listAdapter = ProductListAdapter(view.context, it)
                },{
                    Log.e("Request error", it.message, it)
                    toast(it.toString())
                })
        }
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        val adapter = listAdapter as ProductListAdapter
        switchScreen(ProductDetailsFragment(adapter.products[position], view.top))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val cartItem = menu.findItem(R.id.cart_menu_item)
        basketBadge = BadgeDrawable(cartItem.icon)
        cartItem.setIcon(basketBadge)

        if (badgeCount != basket.size())
            basketBadge!!.setCountAnimated(basket.size())
        else
            basketBadge!!.count = basket.size()

        badgeCount = basket.size()

        basket.addOnBasketChangeListener { basketBadge!!.setCountAnimated(basket.size()) }
    }
}
