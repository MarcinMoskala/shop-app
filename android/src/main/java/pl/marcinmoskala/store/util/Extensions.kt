package pl.marcinmoskala.store.util

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import pl.marcinmoskala.store.App
import pl.marcinmoskala.store.R
import pl.marcinmoskala.store.presentation.basket.BasketFragment
import pl.marcinmoskala.store.presentation.product.ProductDetailsFragment

fun Fragment.switchScreen(fragment: Fragment, animated: Boolean = true, isRoot: Boolean = false) {
    switchScreen(fragmentManager, fragment, animated, isRoot)
}

fun Activity.switchScreen(fragment: Fragment, animated: Boolean = true, isRoot: Boolean = false) {
    switchScreen(fragmentManager, fragment, animated, isRoot)
}

fun Fragment.pref() = (this.activity.application as App).sharedPreferences
fun Fragment.getSavedString(key: String, defVal: String) = pref().getString(key, defVal)
fun Fragment.saveString(key: String, value: String) = pref().edit().putString(key, value).commit()

fun switchScreen(fragmentManager: FragmentManager, fragment: Fragment, animated: Boolean = true, isRoot: Boolean = false) {
    val transaction = fragmentManager.beginTransaction()

    if (animated) {
        transaction.setCustomAnimations(getInAnimationForFragment(fragment), getOutAnimationForFragment(fragment))
    }
    transaction.replace(R.id.contentArea, fragment)
    if (!isRoot) {
        transaction.addToBackStack(null)
    }

    //        setupActionBar(!isRoot)

    transaction.commit()
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