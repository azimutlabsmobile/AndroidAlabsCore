package com.alabs.core_application.utils.wrappers

import androidx.lifecycle.Observer

/**
 * Класс-обертка для событий, которые должны отрабатывать только один раз
 * Необходимо оборачивать [LiveData]-события для [Toast] и для навигации в этот класс, чтобы избежать повторных вызовов
 *
 * Used as a wrapper for data that is exposed via a LiveData that represents an event
 * got from https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 */
open class EventWrapper<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

    /**
     * Returns the content and prevents its use again.
     */
    fun get(): T? = if (hasBeenHandled) {
        null
    } else {
        hasBeenHandled = true
        content
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peek(): T = content
}

/**
 * Can be used for no-data events
 */
object VoidEvent {
    val WRAPPED: EventWrapper<VoidEvent>
        get() = EventWrapper(VoidEvent)
}

/**
 * An [Observer] for [EventWrapper]s, simplifying the pattern of checking if the [EventWrapper]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [EventWrapper]'s contents has not been handled.
 */
class EventObserver<T>(
    private val onEventUnhandledContent: (T) -> Unit
) : Observer<EventWrapper<T>> {
    override fun onChanged(event: EventWrapper<T>?) {
        event?.get()?.let(onEventUnhandledContent)
    }
}