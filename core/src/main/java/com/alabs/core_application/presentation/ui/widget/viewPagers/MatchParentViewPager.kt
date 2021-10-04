package com.alabs.core_application.presentation.ui.widget.viewPagers

import android.content.Context
import android.util.AttributeSet

/**
 * ViewPager c возможность устанавлявать высоту wrap_content
 */
class MatchParentViewPager(context: Context, attrs: AttributeSet?) : ControlSwipeViewPager(context, attrs) {

    var childHeights: MutableList<Int> = ArrayList(childCount)
    var minHeight = 0
    var currentPos = 0

    init {
        setOnPageChangeListener()
        obtainMinHeightAttribute(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpecTmp = heightMeasureSpec
        childHeights.clear()
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            var h = child.measuredHeight
            if (h < minHeight) {
                h = minHeight
            }
            childHeights.add(i, h)
        }
        if (childHeights.size - 1 >= currentPos) {
            heightMeasureSpecTmp =
                MeasureSpec.makeMeasureSpec(childHeights[currentPos], MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpecTmp)
    }

    private fun obtainMinHeightAttribute(
        context: Context,
        attrs: AttributeSet?
    ) {
        val heightAttr = intArrayOf(android.R.attr.minHeight)
        val typedArray = context.obtainStyledAttributes(attrs, heightAttr)
        minHeight = typedArray.getDimensionPixelOffset(0, -666)
        typedArray.recycle()
    }

    private fun setOnPageChangeListener() {
        addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                currentPos = position
                val layoutParams = this@MatchParentViewPager.layoutParams
                layoutParams.height = childHeights[position]
                setLayoutParams(layoutParams)
                this@MatchParentViewPager.invalidate()
            }
        })
    }
}