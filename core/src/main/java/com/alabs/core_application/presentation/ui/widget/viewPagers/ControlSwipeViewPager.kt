package com.alabs.core_application.presentation.ui.widget.viewPagers

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.alabs.core_application.R

/**
 * ViewPager с возможностью блокировки свайпа
 */
open class ControlSwipeViewPager(context: Context, attrs: AttributeSet?) :
    ViewPager(context, attrs) {

    var swipeEnable = true

    init {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.ControlSwipeViewPager, 0, 0)
        try {
            swipeEnable = ta.getBoolean(R.styleable.ControlSwipeViewPager_swipeEnable, true)
        } finally {
            ta.recycle()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnable) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (swipeEnable) {
            super.onInterceptTouchEvent(event)
        } else false
    }

}
