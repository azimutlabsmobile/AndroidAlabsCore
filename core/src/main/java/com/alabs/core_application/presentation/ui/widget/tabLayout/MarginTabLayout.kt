package com.alabs.core_application.presentation.ui.widget.tabLayout

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.alabs.core_application.R

/**
 * Использовать если нужно добавить padding для TabLayout
 */
class MarginTabLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr) {


    private var marginLeft = 0f
    private var marginTop = 0f
    private var marginRight = 0f
    private var marginBottom = 0f


    init {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.MarginTabLayout, 0, 0)
        try {
            marginLeft = ta.getDimension(R.styleable.MarginTabLayout_tabMarginStart, 0f)
            marginTop = ta.getDimension(R.styleable.MarginTabLayout_tabMarginTop, 0f)
            marginRight = ta.getDimension(R.styleable.MarginTabLayout_tabMarginEnd, 0f)
            marginBottom = ta.getDimension(R.styleable.MarginTabLayout_tabMarginBottom, 0f)
        } finally {
            ta.recycle()
        }
    }


    override fun addTab(tab: Tab) {
        super.addTab(tab)
        setTabMargins(marginLeft, marginTop, marginRight, marginBottom)
    }

    override fun addTab(tab: Tab, position: Int) {
        super.addTab(tab, position)
        setTabMargins(marginLeft, marginTop, marginRight, marginBottom)
    }


    override fun addTab(tab: Tab, setSelected: Boolean) {
        super.addTab(tab, setSelected)
        setTabMargins(marginLeft, marginTop, marginRight, marginBottom)
    }


    override fun addTab(tab: Tab, position: Int, setSelected: Boolean) {
        super.addTab(tab, position, setSelected)
        setTabMargins(marginLeft, marginTop, marginRight, marginBottom)
    }

    override fun setupWithViewPager(viewPager: ViewPager?) {
        super.setupWithViewPager(viewPager)
        setTabMargins(marginLeft, marginTop, marginRight, marginBottom)
    }

    override fun setupWithViewPager(viewPager: ViewPager?, autoRefresh: Boolean) {
        super.setupWithViewPager(viewPager, autoRefresh)
        setTabMargins(marginLeft, marginTop, marginRight, marginBottom)

    }

    fun setTabMargins(
        marginLeft: Float = 0f,
        marginTop: Float = 0f,
        marginRight: Float = 0f,
        marginBottom: Float = 0f
    ) {

        this.marginLeft = marginLeft
        this.marginTop = marginTop
        this.marginRight = marginRight
        this.marginBottom = marginBottom

        for (tabPosition in 0 until this.tabCount) {
            val layout = (getChildAt(0) as LinearLayout).getChildAt(tabPosition) as LinearLayout
            val layoutParams = layout.layoutParams as LinearLayout.LayoutParams
            layoutParams.apply {
                weight = 0f
                width = LinearLayout.LayoutParams.WRAP_CONTENT
                setMargins(
                    marginLeft.toInt(),
                    marginTop.toInt(),
                    marginRight.toInt(),
                    marginBottom.toInt()
                )
            }
            layout.layoutParams = layoutParams
        }
    }

}