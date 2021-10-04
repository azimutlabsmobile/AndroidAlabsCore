package com.alabs.core_application.presentation.ui.widget.viewPagers

import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout

// TODO удалить после теста
/**
 * ViewPager с возможностью добавления margin для табов
 */
@Deprecated("использовать MarginTabLayout")
abstract class MarginFragmentPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var tabLayout: TabLayout? = null


    private var top = 0
    private var left = 0
    private var right = 0
    private var bottom = 0

    init {
        setMarginToTabs()
    }


    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        setMarginToTabs()
    }

    fun setMargin(left: Float = 0f, top: Float = 0f, right: Float = 0f, bottom: Float = 0f) {
        this.left = left.toInt()
        this.top = top.toInt()
        this.right = right.toInt()
        this.bottom = bottom.toInt()
    }

    private fun setMarginToTabs() {
        for (i in 0 until (tabLayout?.tabCount ?: 0)) {
            val tab = (tabLayout?.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            tab.requestLayout()
        }
    }


}