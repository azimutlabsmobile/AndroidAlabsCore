package com.alabs.core_application.utils.extensions.view

import androidx.viewpager.widget.ViewPager

/**
 * Слушатель скролла viewPager
 */
fun ViewPager.scrollListener(block: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            block(position)
        }
    })
}