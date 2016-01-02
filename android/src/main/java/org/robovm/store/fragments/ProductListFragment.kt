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

package org.robovm.store.fragments

import android.app.ListFragment
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import org.robovm.store.R
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.model.Basket
import org.robovm.store.model.Product
import org.robovm.store.util.Images
import org.robovm.store.views.BadgeDrawable

class ProductListFragment(val productSelectionListener: (Product, Int)-> Unit) : ListFragment() {
    private var basketBadge: BadgeDrawable? = null
    private var badgeCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        return inflater.inflate(R.layout.robovm_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.setDrawSelectorOnTop(true)
        listView.selector = ColorDrawable(Color.parseColor("#30ffffff"))
        if (listAdapter == null) {
            listAdapter = ProductListViewAdapter(view.context)
            getData()
        }
    }

    private fun getData() {
        val adapter = listAdapter as ProductListViewAdapter
        RoboVMWebService.instance.getProducts { p ->
            adapter.products = p
            RoboVMWebService.instance.preloadProductImages()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val adapter = listAdapter as ProductListViewAdapter
        productSelectionListener.invoke(adapter.products!![position], v.top)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val cartItem = menu.findItem(R.id.cart_menu_item)
        basketBadge = BadgeDrawable(cartItem.icon)
        cartItem.setIcon(basketBadge)

        val basket = RoboVMWebService.instance.basket
        if (badgeCount != basket.size()) {
            basketBadge!!.setCountAnimated(basket.size())
        } else {
            basketBadge!!.count = basket.size()
        }
        badgeCount = basket.size()
        basket.addOnBasketChangeListener( Runnable { basketBadge!!.setCountAnimated(basket.size()) })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private class ProductListViewAdapter(private val context: Context) : BaseAdapter() {
        private val appearInterpolator = DecelerateInterpolator()

        var products: List<Product>? = null
        var newItems: Long = 0

        override fun getCount(): Int {
            return if (products == null) 0 else products!!.size
        }

        override fun getItem(position: Int): Any {
            return products!![position].toString()
        }

        override fun getItemId(position: Int): Long {
            return products!![position].hashCode().toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                convertView = inflater.inflate(R.layout.product_list_item, parent, false)
                convertView!!.id = 1610612736
            }
            convertView.id = convertView.id + 1

            val imageView = convertView.findViewById(R.id.productImage) as ImageView
            val nameLabel = convertView.findViewById(R.id.productTitle) as TextView
            val priceLabel = convertView.findViewById(R.id.productPrice) as TextView
            val progressView = convertView.findViewById(R.id.productImageSpinner) as ProgressBar

            val product = products!![position]
            nameLabel.text = product.name
            priceLabel.text = product.priceDescription

            loadProductImage(convertView, progressView, imageView, product)

            if (1 and newItems.toInt() shr position == 0) {
                newItems = newItems or (1L shl position)
                val density = context.resources.displayMetrics.density
                convertView.translationY = 60 * density
                convertView.rotationX = 12f
                convertView.scaleX = 1.1f
                convertView.pivotY = 180 * density
                convertView.pivotX = (parent.width / 2).toFloat()
                convertView.animate().translationY(0f).rotationX(0f).scaleX(1f).setDuration(450).setInterpolator(appearInterpolator).start()
            }

            return convertView
        }

        private fun loadProductImage(mainView: View, progressView: ProgressBar, imageView: ImageView, product: Product) {
            progressView.visibility = View.VISIBLE
            imageView.setImageResource(android.R.color.transparent)
            Images.setImageFromUrlAsync(imageView, product.imageUrl, Runnable { progressView.visibility = View.INVISIBLE })
        }
    }
}
