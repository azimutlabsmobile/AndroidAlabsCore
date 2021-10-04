package com.alabs.core_application.presentation.ui.widget.layout

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.RelativeLayout
import com.alabs.core_application.R
import com.alabs.core_application.presentation.ui.widget.blurRender.*

open class BlurLayout(context: Context, attrs: AttributeSet?) :
    RelativeLayout(context, attrs) {

    companion object {
        private var RENDERING_COUNT = 0
        private var BLUR_IMPL = 0
        private val STOP_EXCEPTION =
            StopException()
    }


    private var mDownscaleFactor: Float
    private var mOverlayColor: Int
    private var mBlurRadius: Float
    private var cornerRadius = 0f
    private val mBlurImpl: Blur
    private var mDirty = false
    private var mBitmapToBlur: Bitmap? = null
    private var mBlurredBitmap: Bitmap? = null
    private var mBlurringCanvas: Canvas? = null
    private var mIsRendering = false
    private val mPaint: Paint
    private val mRectSrc = Rect()
    private val mRectDst = Rect()
    private var mDecorView: View? = null
    private var mDifferentRoot = false
    private val blurImpl: Blur
        @SuppressLint("ObsoleteSdkInt")
        get() {
            if (BLUR_IMPL == 0) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    try {
                        val impl = AndroidStockBlurImpl()
                        val bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
                        impl.prepare(context, bmp, 4f)
                        impl.release()
                        bmp.recycle()
                        BLUR_IMPL = 3
                    } catch (e: Throwable) {
                    }
                }
            }
            if (BLUR_IMPL == 0) {
                try {
                    javaClass.classLoader?.loadClass("androidx.renderscript.RenderScript")
                    val impl = AndroidXBlurImpl()
                    val bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
                    impl.prepare(context, bmp, 4f)
                    impl.release()
                    bmp.recycle()
                    BLUR_IMPL = 1
                } catch (e: Throwable) {
                    // do nothing
                }
            }
            if (BLUR_IMPL == 0) {
                try {
                    javaClass.classLoader?.loadClass("android.support.v8.renderscript.RenderScript")
                    val impl = SupportLibraryBlurImpl()
                    val bmp = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
                    impl.prepare(context, bmp, 4f)
                    impl.release()
                    bmp.recycle()
                    BLUR_IMPL = 2
                } catch (e: Throwable) {
                }
            }
            if (BLUR_IMPL == 0) {
                // fallback to empty impl, which doesn't have blur effect
                BLUR_IMPL = -1
            }
            return when (BLUR_IMPL) {
                1 -> AndroidXBlurImpl()
                2 -> SupportLibraryBlurImpl()
                3 -> AndroidStockBlurImpl()
                else -> EmptyBlurImpl()
            }
        }


    init {
        setWillNotDraw(false)
        mBlurImpl = blurImpl // provide your own by override getBlurImpl()
        val a = context.obtainStyledAttributes(attrs, R.styleable.BlurLayout)
        mBlurRadius = a.getDimension(
            R.styleable.BlurLayout_blur_blurRadius,
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                context.resources.displayMetrics
            )
        )
        mDownscaleFactor = a.getFloat(R.styleable.BlurLayout_blur_downSampleFactor, 4f)
        mOverlayColor = a.getColor(R.styleable.BlurLayout_blur_overlayColor, -0x55000001)
        cornerRadius = a.getDimension(R.styleable.BlurLayout_blur_cornerRadius, 0f)
        a.recycle()
        mPaint = Paint()
    }

    fun setBlurRadius(radius: Float) {
        if (mBlurRadius != radius) {
            mBlurRadius = radius
            mDirty = true
            invalidate()
        }
    }

    fun setDownsampleFactor(factor: Float) {
        require(factor > 0) { "Downsample factor must be greater than 0." }
        if (mDownscaleFactor != factor) {
            mDownscaleFactor = factor
            mDirty = true // may also change blur radius
            releaseBitmap()
            invalidate()
        }
    }

    fun setOverlayColor(color: Int) {
        if (mOverlayColor != color) {
            mOverlayColor = color
            invalidate()
        }
    }

    private fun releaseBitmap() {
        if (mBitmapToBlur != null) {
            mBitmapToBlur?.recycle()
            mBitmapToBlur = null
        }
        if (mBlurredBitmap != null) {
            mBlurredBitmap?.recycle()
            mBlurredBitmap = null
        }
    }

    protected fun release() {
        releaseBitmap()
        mBlurImpl.release()
    }

    protected fun prepare(): Boolean {
        if (mBlurRadius == 0f) {
            release()
            return false
        }
        var downsampleFactor = mDownscaleFactor
        var radius = mBlurRadius / downsampleFactor
        if (radius > 25) {
            downsampleFactor = downsampleFactor * radius / 25
            radius = 25f
        }
        val width = width
        val height = height
        val scaledWidth = Math.max(1, (width / downsampleFactor).toInt())
        val scaledHeight = Math.max(1, (height / downsampleFactor).toInt())
        var dirty = mDirty
        if (mBlurringCanvas == null || mBlurredBitmap == null || mBlurredBitmap?.width != scaledWidth || mBlurredBitmap?.height != scaledHeight
        ) {
            dirty = true
            releaseBitmap()
            var r = false
            try {
                mBitmapToBlur =
                    Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                if (mBitmapToBlur == null) {
                    return false
                }
                mBlurringCanvas = Canvas(mBitmapToBlur ?: return false)
                mBlurredBitmap =
                    Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                if (mBlurredBitmap == null) {
                    return false
                }
                r = true
            } catch (e: OutOfMemoryError) {
            } finally {
                if (!r) {
                    release()
                    return false
                }
            }
        }
        if (dirty) {
            mDirty = if (mBlurImpl.prepare(context, mBitmapToBlur, radius)) {
                false
            } else {
                return false
            }
        }
        return true
    }

    protected fun blur(bitmapToBlur: Bitmap?, blurredBitmap: Bitmap?) {
        mBlurImpl.blur(bitmapToBlur, blurredBitmap)
    }

    private val preDrawListener =
        ViewTreeObserver.OnPreDrawListener {
            val locations = IntArray(2)
            var oldBmp = mBlurredBitmap
            val decor = mDecorView
            if (decor != null && isShown && prepare()) {
                val redrawBitmap = mBlurredBitmap != oldBmp
                oldBmp = null
                decor.getLocationOnScreen(locations)
                var x = -locations[0]
                var y = -locations[1]
                getLocationOnScreen(locations)
                x += locations[0]
                y += locations[1]

                // just erase transparent
                mBitmapToBlur?.eraseColor(mOverlayColor and 0xffffff)
                val rc = mBlurringCanvas?.save()
                mIsRendering = true
                RENDERING_COUNT++
                try {

                    val w = mBitmapToBlur?.width ?: -1
                    val h = mBitmapToBlur?.height ?: -1
                    mBlurringCanvas?.scale(
                        1f * w / width,
                        1f * h / height
                    )
                    mBlurringCanvas?.translate(-x.toFloat(), -y.toFloat())
                    if (decor.background != null) {
                        mBlurringCanvas?.let { decor.background.draw(it) }
                    }
                    decor.draw(mBlurringCanvas)
                } catch (e: StopException) {
                } finally {
                    mIsRendering = false
                    RENDERING_COUNT--
                    mBlurringCanvas?.restoreToCount(rc ?: -1)
                }
                blur(mBitmapToBlur, mBlurredBitmap)
                if (redrawBitmap || mDifferentRoot) {
                    invalidate()
                }
            }
            true
        }

    protected val activityDecorView: View?
        protected get() {
            var ctx = context
            var i = 0
            while (i < 4 && ctx != null && ctx !is Activity && ctx is ContextWrapper) {
                ctx = ctx.baseContext
                i++
            }
            return if (ctx is Activity) {
                ctx.window.decorView
            } else {
                null
            }
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mDecorView = activityDecorView
        if (mDecorView != null) {
            mDecorView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
            mDifferentRoot = mDecorView?.rootView !== rootView
            if (mDifferentRoot) {
                mDecorView?.postInvalidate()
            }
        } else {
            mDifferentRoot = false
        }
    }

    override fun onDetachedFromWindow() {
        if (mDecorView != null) {
            mDecorView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
        }
        release()
        super.onDetachedFromWindow()
    }

    override fun draw(canvas: Canvas) {
        when {
            mIsRendering -> {
                // Quit here, don't draw views above me
                throw STOP_EXCEPTION
            }
            RENDERING_COUNT > 0 -> {
                // Doesn't support blurview overlap on another blurview
            }
            else -> {
                super.draw(canvas)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBlurredBitmap(canvas, mBlurredBitmap, mOverlayColor)
    }

    /**
     * Custom draw the blurred bitmap and color to define your own shape
     *
     * @param canvas
     * @param blurredBitmap
     * @param overlayColor
     */
    protected fun drawBlurredBitmap(
        canvas: Canvas,
        blurredBitmap: Bitmap?,
        overlayColor: Int
    ) {
        if (blurredBitmap != null) {
            mRectSrc.right = blurredBitmap.width
            mRectSrc.bottom = blurredBitmap.height
            mRectDst.right = width
            mRectDst.bottom = height
            val roundedBitmap = getRoundedCornerBitmap(blurredBitmap, cornerRadius)
            canvas.drawBitmap(roundedBitmap, mRectSrc, mRectDst, null)
        }
        mPaint.color = overlayColor
        val rect = RectF(mRectDst)
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, mPaint)
    }

    private class StopException : RuntimeException()

    private fun getRoundedCornerBitmap(bitmap: Bitmap, radius: Float): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width, bitmap
                .height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect =
            Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, radius, radius, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }


}