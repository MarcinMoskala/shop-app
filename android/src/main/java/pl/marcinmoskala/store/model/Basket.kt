package pl.marcinmoskala.store.model

import java.util.*

class Basket : Iterable<Order> {
    val orders = ArrayList<Order>()
    val basketChangeListeners = ArrayList<()->Unit>()

    fun add(order: Order) {
        orders.add(order)
        onBasketChange()
    }

    fun remove(index: Int): Order {
        val old = orders.removeAt(index)
        onBasketChange()
        return old
    }

    fun clear() {
        orders.clear()
        onBasketChange()
    }

    fun size(): Int {
        return orders.size
    }

    protected fun onBasketChange() {
        for (r in basketChangeListeners) {
            Runnable { r() }.run()
        }
    }

    override fun iterator(): Iterator<Order> {
        return orders.iterator()
    }

    fun addOnBasketChangeListener(listener: ()->Unit) {
        basketChangeListeners.add(listener)
    }

    fun removeOnBasketChangeListener(listener: ()->Unit) {
        basketChangeListeners-=listener
    }
}
