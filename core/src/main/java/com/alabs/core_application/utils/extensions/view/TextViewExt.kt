package com.alabs.core_application.utils.extensions.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.alabs.core_application.presentation.model.UIDrawableClick
import com.alabs.core_application.utils.extensions.showKeyboard

var DURATION = 150L
var ALPHA = 1.0f
fun TextView.expand(container: LinearLayout, iconColor: Int, itemWidth: Int) {
    val bounds = Rect()
    val bubbleWidth = if (itemWidth > 0) itemWidth else bounds.width() + paddingLeft + 10
    container.setCustomBackground(iconColor, ALPHA, true)
    paint.apply {

        getTextBounds(text.toString(), 0, text.length, bounds)
        ValueAnimator.ofInt(0, bubbleWidth).apply {
            addUpdateListener {
                if (it.animatedFraction == (0.0f)) {
                    visibility = View.INVISIBLE
                }
                layoutParams.apply {
                    width = it.animatedValue as Int
                }

                if (it.animatedFraction == (1.0f)) {
                    visibility = View.VISIBLE
                }

                requestLayout()
            }
            interpolator = LinearInterpolator()

            duration = DURATION
        }.start()
    }
}

fun TextView.collapse(
    container: LinearLayout,
    iconColor: Int
) {
    animate().alpha(0f).apply {
        setUpdateListener {
            layoutParams.apply {
                width = (width - (width * it.animatedFraction)).toInt()
            }
            if (it.animatedFraction == 1.0f) {
                visibility = View.GONE
                alpha = 1.0f
            }
            interpolator = LinearInterpolator()
            duration = DURATION
            container.setCustomBackground(iconColor, ALPHA - (ALPHA * it.animatedFraction), false)
            requestLayout()
        }
    }.start()

}

/**
 * Слушатель текстового поля
 */
fun TextView.doOnTextChange(block: (CharSequence?) -> Any) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            // do nothing
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            block(p0)
        }

    })
}

/**
 * Слушатель кликов для иконки
 */
@SuppressLint("ClickableViewAccessibility")
fun TextView.drawableClickListener(
    drawablePosition: UIDrawableClick,
    block: ((View) -> Unit)
) {
    val padding = when (drawablePosition) {
        UIDrawableClick.TOP -> totalPaddingTop
        UIDrawableClick.LEFT -> totalPaddingStart
        UIDrawableClick.RIGHT -> totalPaddingEnd
        UIDrawableClick.BOTTOM -> totalPaddingBottom
    }

    setOnTouchListener { _, event ->
        val listenerExecuted = event.rawX >= right - padding
        if (event.action == MotionEvent.ACTION_UP) {
            if (listenerExecuted) {
                block(this)
            } else {
                requestFocus()
                context.showKeyboard(this)
            }
        }
        listenerExecuted
    }
}

/**
 * Подчеркиваем TextView
 */
fun TextView.underline() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

/**
 * Убираем подчеркивание из TextView
 */
fun TextView.removeUnderline() {
    this.paintFlags = this.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
}

/**
 * Задает цвет текста
 */
fun TextView?.textColor(
    @ColorRes resId: Int
) {
    val context = this?.context ?: return
    this.setTextColor(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.getColor(context, resId)
        } else {
            resources.getColor(resId)
        }
    )
}