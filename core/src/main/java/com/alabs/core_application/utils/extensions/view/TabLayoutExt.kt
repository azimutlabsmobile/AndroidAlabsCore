package com.alabs.core_application.utils.extensions.view

import com.google.android.material.tabs.TabLayout

/**
 * Слушатель для TabLayout
 * @return block возращает выбранный таб
 */
fun TabLayout.selectedListener(block: ((TabLayout.Tab?) -> Unit)) {
    addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            block(tab)
        }
    })
}

/**
 * Слушатель для TabLayout
 * @return block возращает выбранный таб
 */
fun TabLayout.newSelectedListener(block: ((TabLayout.Tab?) -> Unit)) {
    var isInitial = false
    addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (isInitial) {
                block(tab)
            }
            isInitial = true
        }
    })
}

