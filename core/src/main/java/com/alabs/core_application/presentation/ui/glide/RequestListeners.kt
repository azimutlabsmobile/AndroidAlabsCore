package com.alabs.core_application.presentation.ui.glide

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.target.Target


/**
 * Получение результата при обработке SVG
 */
class SvgSoftwareLayerSetter(
    private val block: ((Drawable?) -> Unit?)? = null,
    private val failBlock: ((Boolean) -> Unit?)? = null
) : RequestListener<Drawable?> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable?>,
        isFirstResource: Boolean
    ): Boolean {
        val view: ImageView = (target as ImageViewTarget<*>).view
        view.setLayerType(ImageView.LAYER_TYPE_NONE, null)
        failBlock?.invoke(isFirstResource)
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable?>,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        val view: ImageView = (target as ImageViewTarget<*>).view
        view.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
        block?.invoke(resource)
        return false
    }
}

/**
 * Обработка результата всех изображений
 */
class ImgSoftwareLayer(
    private val block: ((Drawable?) -> Unit?)? = null,
    private val failBlock: ((Boolean) -> Unit?)? = null
) : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        failBlock?.invoke(isFirstResource)
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        block?.invoke(resource)
        return false
    }
}
