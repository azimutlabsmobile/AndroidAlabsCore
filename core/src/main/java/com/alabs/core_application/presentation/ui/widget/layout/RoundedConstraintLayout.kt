package com.alabs.core_application.presentation.ui.widget.layout

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.alabs.core_application.R

class RoundedConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mRadius = 20f

    private val shape = GradientDrawable().apply {
        cornerRadius = mRadius
        setColor((background as? ColorDrawable)?.color ?: android.R.color.black)
    }


    init {
        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.RoundedConstraintLayout, 0, 0)
        try {
            mRadius = ta.getDimension(R.styleable.RoundedConstraintLayout_radius, 20f)
        } finally {
            ta.recycle()
        }
        setWillNotDraw(false)
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