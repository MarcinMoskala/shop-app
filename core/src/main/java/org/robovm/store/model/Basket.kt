package org.robovm.store.model

import java.util.ArrayList

class Basket : Iterable<Order> {
    val orders = ArrayList<Order>()
    val basketChangeListeners = ArrayList<Runnable>()

    fun getOrders(): List<Order> {
        return orders
    }

    fun add(order: Order) {
        orders.add(order)
        onBasketChange()
    }

    operator fun get(index: Int): Order {
        return orders[index]
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
            r.run()
        }
    }

    override fun iterator(): Iterator<Order> {
        return orders.iterator()
    }

    fun addOnBasketChangeListener(listener: Runnable) {
        basketChangeListeners.add(listener)
    }

    fun removeOnBasketChangeListener(listener: Runnable) {
        basketChangeListeners.remove(listener)
    }
}
