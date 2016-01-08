package org.robovm.store

import android.app.Fragment
import android.app.FragmentManager
import org.robovm.store.api.RoboVMWebService
import org.robovm.store.fragments.*
import org.robovm.store.model.Product

class FragmentSwitch{

    fun switchScreens(fragmentManager: FragmentManager, fragment: Fragment, animated: Boolean = true, isRoot: Boolean = false): Int {
        val transaction = fragmentManager.beginTransaction()

        if (animated) {
            transaction.setCustomAnimations(getInAnimationForFragment(fragment), getOutAnimationForFragment(fragment))
        }
        transaction.replace(R.id.contentArea, fragment)

        if (!isRoot) {
            transaction.addToBackStack(null)
        }

//        setupActionBar(!isRoot)

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
}