package com.alabs.core_application.presentation.ui.glide.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.caverock.androidsvg.SVG
import com.alabs.core_application.presentation.ui.glide.SvgBitmapTranscoder
import com.alabs.core_application.presentation.ui.glide.SvgDecoder
import com.alabs.core_application.presentation.ui.glide.SvgDrawableTranscoder
import com.alabs.core_application.presentation.ui.glide.SvgPictureDrawableTranscoder
import java.io.InputStream


@GlideModule
class SvgModule : AppGlideModule() {
    override fun registerComponents(
        context: Context, glide: Glide, registry: Registry
    ) {
        registry
            .register(SVG::class.java, Drawable::class.java, SvgDrawableTranscoder(context))
            .register(SVG::class.java, PictureDrawable::class.java, SvgPictureDrawableTranscoder())
            .register(SVG::class.java, Bitmap::class.java, SvgBitmapTranscoder())
            .append(InputStream::class.java, SVG::class.java, SvgDecoder())
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
