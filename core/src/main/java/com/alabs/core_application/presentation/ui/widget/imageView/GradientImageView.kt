package com.alabs.core_application.presentation.ui.widget.imageView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.alabs.core_application.R


class GradientImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var path: Path? = null
    private var rect: RectF? = null
    private var startX = 0f
    private var startY = 0f
    private var widthRatio = 1.0f
    private var heightRatio = 1.0f
    private var rotate = 0.0f
    private var startColor = Color.parseColor("#00000000")
    private var endColor = Color.parseColor("#FF000000")
    private var middleColor = -1
    private var gradientAlpha = 1.0f
    private var startOffset = 0.0f
    private var endOffset = 1.0f
    private var middleOffset = 0.5f
    private var cornerRadius: Float = 0f
    var colors: IntArray? = null
    var offsets: FloatArray? = null
    var gradient: Shader? = null
    var rotateMatrix: Matrix? = null
    var gradientPaint: Paint? = null


    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.GradientImageView)
        startX = array.getFloat(R.styleable.GradientImageView_giv_x, startX)
        startY = array.getFloat(R.styleable.GradientImageView_giv_y, startY)
        cornerRadius = array.getDimension(R.styleable.GradientImageView_radius, 0f)
        widthRatio = array.getFloat(R.styleable.GradientImageView_giv_width, widthRatio)
        heightRatio = array.getFloat(R.styleable.GradientImageView_giv_height, heightRatio)
        rotate = array.getFloat(R.styleable.GradientImageView_giv_rotate, rotate)
        startColor = array.getColor(R.styleable.GradientImageView_giv_startColor, startColor)
        endColor = array.getColor(R.styleable.GradientImageView_giv_endColor, endColor)
        middleColor = array.getColor(R.styleable.GradientImageView_giv_middleColor, middleColor)
        startOffset = array.getFloat(R.styleable.GradientImageView_giv_startOffset, startOffset)
        endOffset = array.getFloat(R.styleable.GradientImageView_giv_endOffset, endOffset)
        middleOffset = array.getFloat(R.styleable.GradientImageView_giv_middleOffset, middleOffset)
        gradientAlpha = array.getFloat(R.styleable.GradientImageView_giv_alpha, gradientAlpha)
        array.recycle()
        initColors()
        gradientPaint = Paint()
        rotateMatrix = Matrix()
        path = Path()
    }


    override fun onDraw(canvas: Canvas) {
        rect = RectF(0.0f, 0.0f, this.width.toFloat(), this.height.toFloat())
        path?.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        path?.let {
            canvas.clipPath(it)
        }
        super.onDraw(canvas)
        val left = startX * width
        val top = startY * height
        val right = left + widthRatio * width
        val bottom = top + heightRatio * height
        gradient = LinearGradient(
            left, top,
            right, bottom,
            colors ?: intArrayOf(),
            offsets,
            Shader.TileMode.CLAMP
        )
        rotateMatrix?.setRotate(rotate, width / 2.toFloat(), height / 2.toFloat())
        gradient?.setLocalMatrix(rotateMatrix)
        gradientPaint?.shader = gradient
        gradientPaint?.alpha = (gradientAlpha * 255).toInt()
        canvas.drawRect(left, top, right, bottom, gradientPaint ?: return)
    }

    /**
     * Provide get/set methods for Property Animation
     */
    fun getRotate(): Float {
        return rotate
    }

    fun setRotate(rotate: Float) {
        this.rotate = rotate
        gradient = null
        postInvalidate()
    }

    fun getStartX(): Float {
        return startX
    }

    fun setStartX(startX: Float) {
        this.startX = startX
        gradient = null
        postInvalidate()
    }

    fun getStartY(): Float {
        return startY
    }

    fun setStartY(startY: Float) {
        this.startY = startY
        gradient = null
        postInvalidate()
    }

    fun getWidthRatio(): Float {
        return widthRatio
    }

    fun setWidthRatio(widthRatio: Float) {
        this.widthRatio = widthRatio
        gradient = null
        postInvalidate()
    }

    fun getHeightRatio(): Float {
        return heightRatio
    }

    fun setHeightRatio(heightRatio: Float) {
        this.heightRatio = heightRatio
        gradient = null
        postInvalidate()
    }

    fun getGradientAlpha(): Float {
        return gradientAlpha
    }

    fun setGradientAlpha(gradientAlpha: Float) {
        this.gradientAlpha = gradientAlpha
        postInvalidate()
    }

    fun setStartColor(color: Int) {
        startColor = color
        initColors()
        postInvalidate()
    }

    fun setMiddleColor(color: Int) {
        middleColor = color
        initColors()
        postInvalidate()
    }

    fun setEndColor(color: Int) {
        endColor = color
        initColors()
        postInvalidate()
    }

    private fun initColors() {
        if (middleColor == -1) {
            colors = intArrayOf(startColor, endColor)
            offsets = floatArrayOf(startOffset, endOffset)
        } else {
            colors = intArrayOf(startColor, middleColor, endColor)
            offsets = floatArrayOf(startOffset, middleOffset, endOffset)
        }
    }
}