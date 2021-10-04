package com.alabs.core_application.presentation.ui.widget.blurRender

import android.content.Context
import android.graphics.Bitmap

class EmptyBlurImpl :
    Blur {
    override fun prepare(
        context: Context?,
        buffer: Bitmap?,
        radius: Float
    ): Boolean {
        return false
    }

    override fun release() {}
    override fun blur(input: Bitmap?, output: Bitmap?) {}
}