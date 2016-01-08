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

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Fragment
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.*
import android.widget.*
import org.robovm.store.R
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.model.*
import org.robovm.store.util.*
import org.robovm.store.views.BadgeDrawable
import org.robovm.store.views.KenBurnsDrawable
import org.robovm.store.views.SlidingLayout

import java.util.ArrayList
import java.util.Collections
import java.util.Random

class ProductDetailsFragment : Fragment, ViewTreeObserver.OnGlobalLayoutListener {
    private var currentProduct: Product? = null
    private var order: Order? = null

    private var productImage: ImageView? = null

    private val random = Random()
    private var currentIndex: Int = 0
    private var shouldAnimatePop: Boolean = false
    private var basketBadge: BadgeDrawable? = null
    private var images = ArrayList<String>()
    private var cached: Boolean = false
    private var slidingDelta: Int? = null
    private var sizeSpinner: Spinner? = null
    private var colorSpinner: Spinner? = null

    private var productDrawable: KenBurnsDrawable? = null
    private var kenBurnsMovement: ValueAnimator? = null
    private var kenBurnsAlpha: ValueAnimator? = null

    constructor(product: Product, slidingDelta: Int) {
        this.currentProduct = product
        this.slidingDelta = slidingDelta
        this.order = Order(product)

        images = ArrayList(product.imageUrls)
        Collections.shuffle(images)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.product_detail, null, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productImage = view.findViewById(R.id.productImage) as ImageView
        sizeSpinner = view.findViewById(R.id.productSize) as Spinner
        colorSpinner = view.findViewById(R.id.productColor) as Spinner

        (view.findViewById(R.id.addToBasket) as Button).setOnClickListener { button ->
            order?.productSize = currentProduct!!.sizes[sizeSpinner!!.selectedItemPosition]
            order?.productColor = currentProduct!!.colors[colorSpinner!!.selectedItemPosition]
            shouldAnimatePop = true
            activity.fragmentManager.popBackStack()
            RoboVMWebService.instance.basket.add(Order(order!!))
        }

        (view.findViewById(R.id.productTitle) as TextView).text = currentProduct!!.name
        (view.findViewById(R.id.productPrice) as TextView).text = currentProduct!!.priceDescription
        (view.findViewById(R.id.productDescription) as TextView).text = currentProduct!!.description

        (view as SlidingLayout).initialMainViewDelta = slidingDelta!!

        loadOptions()
    }

    private fun loadOptions() {
        val sizeAdapter = ArrayAdapter(activity,
                android.R.layout.simple_spinner_dropdown_item, currentProduct!!.sizes)
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        sizeSpinner?.adapter = sizeAdapter

        val colorAdapter = ArrayAdapter(activity,
                android.R.layout.simple_spinner_dropdown_item, currentProduct!!.colors)
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        colorSpinner?.adapter = colorAdapter
    }

    override fun onStart() {
        super.onStart()
        animateImages()
    }

    override fun onStop() {
        super.onStop()
        kenBurnsAlpha?.cancel()
        kenBurnsMovement?.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val cartItem = menu.findItem(R.id.cart_menu_item)
        basketBadge = BadgeDrawable(cartItem.icon)
        cartItem.setIcon(basketBadge)

        val basket = RoboVMWebService.instance.basket
        basketBadge!!.count = basket.size()
        basket.addOnBasketChangeListener { basketBadge!!.setCountAnimated(basket.size()) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        if (!enter && shouldAnimatePop) {
            return AnimatorInflater.loadAnimator(view!!.context, R.anim.add_to_basket_in)
        }
        return super.onCreateAnimator(transit, enter, nextAnim)
    }

    private fun animateImages() {
        if (images.size < 1) return
        if (images.size == 1) {
            Images.setImageFromUrlAsync(productImage!!, images[0])
            return
        }
        productImage?.viewTreeObserver?.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        productImage?.viewTreeObserver?.removeOnGlobalLayoutListener(this)

        Thread {
            val img1 = Images.fromUrl(images[0])
            val img2 = Images.fromUrl(images[1])

            val activity = activity
            activity?.runOnUiThread {
                productDrawable = KenBurnsDrawable(Colors.Green)
                productDrawable!!.firstBitmap = img1
                productDrawable!!.secondBitmap = img2
                productImage!!.setImageDrawable(productDrawable)
                currentIndex++

                // Check for null bitmaps due to decode errors:
                if (productDrawable!!.firstBitmap != null) {
                    var resizeRatio = -1f
                    var widthDiff = -1f
                    var heightDiff = -1f
                    var zoomInX = -1f
                    var zoomInY = -1f
                    var moveX = -1f
                    var moveY = -1f

                    val frameWidth = productImage!!.width.toFloat()
                    val frameHeight = productImage!!.height.toFloat()

                    val imageWidth = productDrawable!!.firstBitmap!!.width.toFloat()
                    val imageHeight = productDrawable!!.firstBitmap!!.height.toFloat()

                    // Wider than screen
                    if (imageWidth > frameWidth) {
                        widthDiff = imageWidth - frameWidth

                        // Higher than screen
                        if (imageHeight > frameHeight) {
                            heightDiff = imageHeight - frameHeight

                            resizeRatio = if (widthDiff > heightDiff)
                                frameHeight / imageHeight
                            else
                                frameWidth / imageWidth
                        } else {
                            heightDiff = frameHeight - imageHeight

                            resizeRatio = if (widthDiff > heightDiff)
                                frameWidth / imageWidth
                            else
                                frameHeight / imageHeight
                        }
                        // No wider than screen
                    } else {
                        widthDiff = frameWidth - imageWidth

                        // Higher than screen
                        if (imageHeight > frameHeight) {
                            heightDiff = imageHeight - frameHeight

                            resizeRatio = if (widthDiff > heightDiff)
                                imageHeight / frameHeight
                            else
                                frameWidth / imageWidth
                        } else {
                            heightDiff = frameHeight - imageHeight

                            resizeRatio = if (widthDiff > heightDiff)
                                frameWidth / imageWidth
                            else
                                frameHeight / imageHeight
                        }
                    }

                    // Resize the image.
                    val optimusWidth = imageWidth * resizeRatio * ENLARGE_RATIO
                    val optimusHeight = imageHeight * resizeRatio * ENLARGE_RATIO

                    val originX = (frameWidth - optimusWidth) / 2
                    var originY = 0f

                    val maxMoveX = Math.min(optimusWidth - frameWidth, 50f)
                    val maxMoveY = Math.min(optimusHeight - frameHeight, 50f) * 2f / 3

                    val rotation = random.nextInt(9) / 100f

                    when (random.nextInt(3)) {
                        0 -> {
                            zoomInX = 1.25f
                            zoomInY = 1.25f
                            moveX = -maxMoveX
                            moveY = -maxMoveY
                        }
                        1 -> {
                            zoomInX = 1.1f
                            zoomInY = 1.1f
                            moveX = -maxMoveX
                            moveY = maxMoveY
                            originY = -moveY * zoomInY * 1.1f
                        }
                        2 -> {
                            zoomInX = 1.2f
                            zoomInY = 1.2f
                            moveX = 0f
                            moveY = -maxMoveY
                        }
                        else -> {
                            zoomInX = 1.2f
                            zoomInY = 1.2f
                            moveX = 0f
                            moveY = maxMoveY
                            originY = -moveY * zoomInY * 1.1f
                        }
                    }

                    val evaluator = MatrixEvaluator()
                    val startMatrix = Matrix()
                    startMatrix.setTranslate(originX, originY)
                    startMatrix.postScale(resizeRatio * ENLARGE_RATIO, resizeRatio * ENLARGE_RATIO, originX, originY)

                    val finalMatrix = Matrix()
                    finalMatrix.setTranslate(originX + moveX, originY + moveY)
                    finalMatrix.postScale(resizeRatio * ENLARGE_RATIO * zoomInX, resizeRatio * ENLARGE_RATIO * zoomInY,
                            originX, originY)
                    finalMatrix.postRotate(rotation)

                    kenBurnsMovement = ValueAnimator.ofObject(evaluator, startMatrix, finalMatrix)
                    kenBurnsMovement!!.addUpdateListener { animator -> productDrawable!!.setMatrix(animator.animatedValue as Matrix) }
                    kenBurnsMovement!!.setDuration(14000)
                    kenBurnsMovement!!.repeatMode = ValueAnimator.REVERSE
                    kenBurnsMovement!!.repeatCount = ValueAnimator.INFINITE
                    kenBurnsMovement!!.start()

                    kenBurnsAlpha = ObjectAnimator.ofInt(productDrawable, "alpha", 0, 0, 0, 255, 255, 255)
                    kenBurnsAlpha!!.setDuration(kenBurnsMovement!!.duration)
                    kenBurnsAlpha!!.repeatMode = ValueAnimator.REVERSE
                    kenBurnsAlpha!!.repeatCount = ValueAnimator.INFINITE
                    kenBurnsAlpha!!.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {
                        }

                        override fun onAnimationEnd(animation: Animator) {
                        }

                        override fun onAnimationCancel(animation: Animator) {
                        }

                        override fun onAnimationRepeat(animation: Animator) {
                            nextImage()
                        }
                    })
                    kenBurnsAlpha!!.start()
                }
            }
        }.start()
    }

    private fun nextImage() {
        currentIndex = (currentIndex + 1) % images.size
        val image = images[currentIndex]
        Images.setImageFromUrlAsync(productDrawable!!, image)
        precacheNextImage()
    }

    private fun precacheNextImage() {
        if (currentIndex + 1 >= images.size) {
            cached = true
        }
        if (cached) {
            return
        }
        val next = currentIndex + 1
        val image = images[next]
        ImageCache.instance.downloadImage(image) { f -> }
    }

    companion object {
        private val ENLARGE_RATIO = 1.1f
    }
}
