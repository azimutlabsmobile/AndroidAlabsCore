package com.alabs.core_application.utils.extensions.view

import android.view.View
import androidx.core.widget.NestedScrollView

/**
 * Слушатель NestedScrollView срабатывает когда сколл доходит до конца страници
 */
fun NestedScrollView.doOnEndPage(block: () -> Unit) {
    this.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
        val view = v.getChildAt(v.childCount - 1) as View
        val diff: Int = view.bottom - (v.height + v.scrollY)
        if (diff == 0) {
            block()
        }
    }
}