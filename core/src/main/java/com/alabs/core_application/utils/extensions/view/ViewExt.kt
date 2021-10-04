package com.alabs.core_application.utils.extensions.view

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.KeyEvent
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.drawable.DrawableCompat
import com.alabs.core_application.presentation.ui.widget.clickListeners.SafeClickListener

/**
 * Created by Yergali Zhakhan on 5/20/20.
 */

/**
 * Блокировка множественного нажатия
 */
fun View.safeClickListener(block: (View) -> Unit) {
    setOnClickListener(SafeClickListener(onSafeCLick = {
        block(it)
    }))
}

fun View.setCustomBackground(color: Int, alpha: Float, isExpand: Boolean) {
    val containerBackground = GradientDrawable().apply {
        cornerRadius = 100f
        DrawableCompat.setTint(
            this,
            Color.argb(
                (Color.alpha(color) * alpha).toInt(),
                Color.red(color),
                Color.green(color),
                Color.blue(color)
            )
        )
    }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        containerBackground.setColor(if (isExpand) color else Color.TRANSPARENT)
    }
    background = containerBackground
}

/**
 * Анимированный показ/удаление view
 * @param value View.GONE - скрыть View.VISIBLE - показать
 */
fun View.animationVisibility(value: Int) {
    val valueAnimator = when (value) {
        View.VISIBLE -> {
            ValueAnimator.ofFloat(0f, 1f).apply {
                doOnStart {
                    this@animationVisibility.visibility = View.VISIBLE
                }
            }
        }
        View.GONE -> {
            ValueAnimator.ofFloat(1f, 0f).apply {
                doOnEnd {
                    this@animationVisibility.visibility = View.GONE
                }
            }
        }
        else -> null
    }

    valueAnimator?.addUpdateListener {
        this.alpha = it.animatedValue as Float
    }

    val isRunning = valueAnimator?.isRunning ?: true
    if (!isRunning)
        valueAnimator?.start()
}

/**
 * Перехватчик нажатия на кнопку назад в navigation кнопках телефона
 */
fun View.onKeyBackPressed(block: () -> Unit) {
    this.isFocusableInTouchMode = true
    this.requestFocus()
    this.setOnKeyListener(object : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            return if (keyCode == KeyEvent.KEYCODE_BACK) {
                block()
                true
            } else {
                false
            }
        }
    })
}

/**
 * Делаем view неактивной
 * @param isViewEnabled true view активная false неактивная
 */
fun View.setStateEnable(isViewEnabled: Boolean) {
    this.isClickable = isViewEnabled
    this.isEnabled = isViewEnabled
    this.alpha = if (isViewEnabled) 1.0f else 0.4f
}