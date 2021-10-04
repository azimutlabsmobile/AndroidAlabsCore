package com.alabs.core_application.presentation.ui.widget.blurRender

import android.content.Context
import android.graphics.Bitmap

interface Blur {
    fun prepare(
        context: Context?,
        buffer: Bitmap?,
        radius: Float
    ): Boolean

    fun release()
    fun blur(input: Bitmap?, output: Bitmap?)
}