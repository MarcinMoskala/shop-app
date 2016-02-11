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

package pl.marcinmoskala.store.presentation.product

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.*
import butterknife.bindView
import com.bumptech.glide.Glide
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.basket
import pl.marcinmoskala.store.model.Order
import pl.marcinmoskala.store.model.Product
import pl.marcinmoskala.store.views.BadgeDrawable
import pl.marcinmoskala.store.views.SlidingLayout
import java.util.*
import java.util.Collections.shuffle

class ProductDetailsFragment(val currentProduct: Product, val slidingDelta: Int) : Fragment() {
    private var order: Order = Order(currentProduct)

    private val productImage: ImageView by bindView(R.id.productImage)
    private val sizeSpinner: Spinner by bindView(R.id.productSize)

    private var shouldAnimatePop: Boolean = false
    private var images = ArrayList(currentProduct.imageUrls)

    init {
        shuffle(images)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.product_detail, null, true)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (view.findViewById(R.id.addToBasket) as Button).setOnClickListener { button ->
            order.size = currentProduct.sizes[sizeSpinner.selectedItemPosition]
            shouldAnimatePop = true

            activity.fragmentManager.popBackStack()
            basket.add(order.copy())
//            setupActionBar()
        }

        (view.findViewById(R.id.productTitle) as TextView).text = currentProduct.name
        (view.findViewById(R.id.productPrice) as TextView).text = currentProduct.price.toString()
        (view.findViewById(R.id.productDescription) as TextView).text = currentProduct.description

        (view as SlidingLayout).initialMainViewDelta = slidingDelta

        val sizeAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, currentProduct.sizes)
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sizeSpinner.adapter = sizeAdapter

        Glide.with(this)
                .load(images[0])
                .into(productImage)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val cartItem = menu.findItem(R.id.cart_menu_item)
        val basketBadge = BadgeDrawable(cartItem.icon)
        cartItem.icon = basketBadge
        basketBadge.count = basket.size()
        basket.addOnBasketChangeListener { basketBadge.setCountAnimated(basket.size()) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? =
        if (!enter && shouldAnimatePop)
            AnimatorInflater.loadAnimator(view!!.context, R.anim.add_to_basket_in)
        else
            null
}
