package com.alabs.core_application.presentation.ui.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder
import com.caverock.androidsvg.SVG

/**
 * Тринсформируем из svg в PictureDrawable
 */
class SvgPictureDrawableTranscoder : ResourceTranscoder<SVG?, PictureDrawable?> {

    override fun transcode(
        toTranscode: Resource<SVG?>,
        options: Options
    ): Resource<PictureDrawable?>? {
        val svg: SVG = toTranscode.get()
        val picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)
        return SimpleResource(drawable)
    }
}

/**
 * Тринсформируем из svg в Bitmap
 */
class SvgBitmapTranscoder : ResourceTranscoder<SVG, Bitmap> {

    override fun transcode(
        toTranscode: Resource<SVG>,
        options: Options
    ): Resource<Bitmap?> {
        val svg: SVG = toTranscode.get()
        val picture = svg.renderToPicture()
        val drawable = PictureDrawable(picture)
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        canvas.drawPicture(drawable.picture)
        return SimpleResource(bitmap)
    }
}

/**
 * Тринсформируем из svg в Drawable
 */
class SvgDrawableTranscoder(
    private val context: Context
) : ResourceTranscoder<SVG, Drawable> {

    private val bitmapTranscoder = SvgBitmapTranscoder()

    override fun transcode(toTranscode: Resource<SVG>, options: Options): Resource<Drawable> {
        val bitmap = bitmapTranscoder.transcode(toTranscode, options).get()

        return SimpleResource(BitmapDrawable(context.resources, bitmap))
    }
}