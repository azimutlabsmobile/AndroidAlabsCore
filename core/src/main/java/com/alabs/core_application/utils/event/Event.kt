package com.alabs.core_application.utils.event

import androidx.lifecycle.Observer

/**
 * Обрабатывает результат [Pair] у LiveData, при получении значение null любого из значение [Pair] событие не будет обработанно
 */
class PairNotNullObserver<T, V>(private val block: (Pair<T, V>) -> Unit) : Observer<Pair<T, V>> {
    override fun onChanged(t: Pair<T, V>?) {
        if (t?.second == null || t?.first == null) {
            return
        }
        t.let(block)
    }

}

/**
 * Любое полученное событие null не будет обработанно
 */
class NotNullObserver<T>(private val block: (T) -> Unit) : Observer<T> {
    override fun onChanged(t: T): Unit {
        t?.let(block)
    }
}