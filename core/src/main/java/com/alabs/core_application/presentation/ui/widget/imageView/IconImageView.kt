package com.alabs.core_application.presentation.ui.widget.imageView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.alabs.core_application.R

/**
 * Округленный ImageView с заданной иконкой и радиусом округления
 */
class IconImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var radius: Float = 0f
    private var imageHeight = 0f
    private var imageWidth = 0f
    private var icon: Drawable? = null

    init {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.IconImageView)
        try {
            radius = arr.getDimension(R.styleable.IconImageView_radius, 0f)
            imageWidth = arr.getDimension(R.styleable.IconImageView_imageWidth, 0f)
            imageHeight = arr.getDimension(R.styleable.IconImageView_imageHeight, 0f)
            icon = arr.getDrawable(R.styleable.IconImageView_icon)
        } finally {
            arr.recycle()
        }
    }


    private val shape = GradientDrawable().apply {
        cornerRadius = radius
        setColor((background as? ColorDrawable)?.color ?: android.R.color.black)
    }


    override fun setBackgroundColor(color: Int) {
        shape.setColor(color)
        background = shape
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawIcon(canvas)
        }
        drawBg()
    }

    private fun drawBg() {
        background = shape
    }

    private fun drawIcon(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        icon?.setBounds(
            width / 2 - imageWidth.toInt() / 2,
            height / 2 - imageHeight.toInt() / 2,
            width / 2 + imageWidth.toInt() / 2,
            height / 2 + imageHeight.toInt() / 2
        )
        icon?.draw(canvas)
    }


}