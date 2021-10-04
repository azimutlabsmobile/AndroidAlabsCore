package com.alabs.core_application.presentation.ui.widget.textViews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.widget.TextView
import com.alabs.core_application.R



class RoundedTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private var mRadius = 20f

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedTextView, 0, 0)
        try {
            mRadius = ta.getDimension(R.styleable.RoundedTextView_radius, 20f)
        } finally {
            ta.recycle()
        }
        setWillNotDraw(false)
    }


    private val shape = GradientDrawable().apply {
        cornerRadius = mRadius
        setColor((background as? ColorDrawable)?.color ?: android.R.color.black)
    }


    override fun setBackgroundColor(color: Int) {
        shape.setColor(color)
        background = shape
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBg()
    }

    private fun drawBg() {
        background = shape
    }


}