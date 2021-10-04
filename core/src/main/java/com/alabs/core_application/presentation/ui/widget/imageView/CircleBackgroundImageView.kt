package com.alabs.core_application.presentation.ui.widget.imageView

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.alabs.core_application.R

/**
 * Круглый фон в виде цвета у картинки
 */
class CircleBackgroundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var size = 0f

    @DrawableRes
    private var imageRes: Int? = null
    private var imageDrawable: Drawable? = null
    private var bgColor: Int = (background as? ColorDrawable)?.color ?: android.R.color.transparent
    private var imageWith = 0f
    private var imageHeight = 0f

    init {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.CircleBackgroundImageView)
        imageDrawable = arr.getDrawable(R.styleable.CircleBackgroundImageView_icon)

        imageWith = arr.getDimension(
            R.styleable.CircleBackgroundImageView_imageWidth,
            resources.getDimension(R.dimen.dp_24)
        )
        imageHeight = arr.getDimension(
            R.styleable.CircleBackgroundImageView_imageHeight,
            resources.getDimension(R.dimen.dp_24)
        )

        arr.recycle()
        setOnMeasureCallback()
    }

    private val drawPaint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(size, size, size, drawPaint)
        drawIconView(canvas)
    }

    private fun setOnMeasureCallback() {
        val vto = viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                removeOnGlobalLayoutListener(this)
                size = measuredWidth / 2.toFloat()
            }
        })
    }


    private fun drawIconView(canvas: Canvas?) {

        if (imageRes == null && imageDrawable == null) {
            return
        }
        val mMyVectorDrawable =
            imageDrawable ?: ResourcesCompat.getDrawable(resources, imageRes ?: 0, null)


        val w = canvas?.width ?: 0
        val h = canvas?.height ?: 0

        mMyVectorDrawable?.setBounds(
            w / 2 - imageWith.toInt() / 2,
            h / 2 - imageHeight.toInt() / 2,
            w / 2 + imageWith.toInt() / 2,
            h / 2 + imageWith.toInt() / 2
        )
        canvas?.let {
            mMyVectorDrawable?.draw(canvas)
        }
    }

    private fun removeOnGlobalLayoutListener(listener: OnGlobalLayoutListener) {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    fun setImage(@DrawableRes image: Int) {
        this.imageRes = image
        invalidate()
    }

    fun setImage(drawable: Drawable) {
        this.imageDrawable = drawable
    }


    override fun setBackgroundColor(color: Int) {
        drawPaint.color = color
        bgColor = color
        invalidate()
    }

}