package com.alabs.core_application.presentation.ui.widget.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.LinearLayout
import com.alabs.core_application.R


class RoundedLinearLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var mRadius = 20f

    init {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.RoundedLinearLayout, 0, 0)
        try {
            mRadius = ta.getDimension(R.styleable.RoundedLinearLayout_radius, 20f)
        } finally {
            ta.recycle()
        }
        setWillNotDraw(false)
    }

    private val shape = GradientDrawable().apply {
        cornerRadius = mRadius
        setColor((background as? ColorDrawable)?.color ?: android.R.color.black)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBg()
    }

    override fun setBackgroundColor(color: Int) {
        shape.setColor(color)
        background = shape
        invalidate()
    }

    private fun drawBg() {
        background = shape
    }

}


