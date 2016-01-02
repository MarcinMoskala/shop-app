package org.robovm.store.util

object Objects {
    fun <T> requireNonNull(obj: T?): T {
        if (obj == null)
            throw NullPointerException()
        return obj
    }

    fun <T> requireNonNull(obj: T?, message: String): T {
        if (obj == null)
            throw NullPointerException(message)
        return obj
    }
}
