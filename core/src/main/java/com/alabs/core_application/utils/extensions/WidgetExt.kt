package com.alabs.core_application.utils.extensions

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.alabs.core_application.presentation.ui.widget.clickListeners.SafeClickListener

/**
 * Блокировка множественного нажатия
 */
fun View.safeClickListener(block: (View) -> Unit) {
    setOnClickListener(SafeClickListener(onSafeCLick = {
        block(it)
    }))
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