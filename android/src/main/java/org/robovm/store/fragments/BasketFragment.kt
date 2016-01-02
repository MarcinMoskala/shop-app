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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import org.robovm.store.R
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.model.Basket
import org.robovm.store.model.Order
import org.robovm.store.util.Images
import org.robovm.store.views.SwipableListItem
import org.robovm.store.views.ViewSwipeTouchListener

class BasketFragment : ListFragment() {
    private val basket: Basket
    private var checkoutButton: Button? = null
    private var checkoutListener: Runnable? = null

    init {
        this.basket = RoboVMWebService.instance.basket
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        val shoppingCartView = inflater.inflate(R.layout.basket, container, false)

        checkoutButton = shoppingCartView.findViewById(R.id.checkoutBtn) as Button
        checkoutButton!!.setOnClickListener { b ->
            checkoutListener?.run()
        }
        shoppingCartView.pivotY = 0f
        shoppingCartView.pivotX = container!!.width.toFloat()

        return shoppingCartView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.dividerHeight = 0
        listView.divider = null
        listAdapter = GroceryListAdapter(view.context, basket)
        if (listAdapter.count == 0) {
            checkoutButton!!.visibility = INVISIBLE
        }

        basket.addOnBasketChangeListener( Runnable { checkoutButton!!.visibility = if (basket.size() > 0) VISIBLE else INVISIBLE })
    }

    class GroceryListAdapter(private val context: Context, private val basket: Basket) : BaseAdapter() {

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return basket.get(position).toString()
        }

        override fun getCount(): Int {
            return basket.size()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val order = basket.get(position)

            var view: View? = convertView // re-use an existing view, if one is available
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false)
                val swipper = (view as SwipableListItem).swipeListener
                val finalView = view
                swipper!!.addEventListener(object : ViewSwipeTouchListener.EventListener {
                    override fun onSwipeGestureBegin() {
                        parent.requestDisallowInterceptTouchEvent(true)
                    }

                    override fun onSwipeGestureEnd() {
                        parent.requestDisallowInterceptTouchEvent(false)
                    }

                    override fun onItemSwipped() {
                        // If view has already been processed, do nothing
                        if (finalView.getParent() == null) {
                            return
                        }
                        val p = (parent as ListView).getPositionForView(finalView)
                        val basket = RoboVMWebService.instance.basket
                        basket.remove(p)
                        notifyDataSetChanged()
                    }
                })
            }

            (view.findViewById(R.id.productTitle) as TextView).text = order.product!!.name
            (view.findViewById(R.id.productPrice) as TextView).text = order.product!!.priceDescription
            (view.findViewById(R.id.productColor) as TextView).text = order.color
            (view.findViewById(R.id.productSize) as TextView).text = order.size

            val orderImage = view.findViewById(R.id.productImage) as ImageView
            orderImage.setImageResource(R.drawable.product_image)

            Images.setImageFromUrlAsync(orderImage, order.productColor!!.imageUrls[0])

            return view
        }
    }

    fun setCheckoutListener(checkoutClickedListener: Runnable) {
        this.checkoutListener = checkoutClickedListener
    }
}
