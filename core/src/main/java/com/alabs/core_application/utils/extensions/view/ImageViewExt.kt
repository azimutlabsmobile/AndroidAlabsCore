package com.alabs.core_application.utils.extensions.view

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.StateListAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import com.alabs.core_application.data.constants.FileConstant
import com.alabs.core_application.presentation.ui.glide.ImgSoftwareLayer
import com.alabs.core_application.presentation.ui.glide.SvgSoftwareLayerSetter
import com.alabs.core_application.utils.os.getFileExtension
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@SuppressLint("RestrictedApi")
fun ImageView.colorAnimator(
    @ColorInt from: Int,
    @ColorInt to: Int,
    durationInMillis: Long
): Animator = ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
    duration = durationInMillis
    addUpdateListener { animator ->
        val color = animator.animatedValue as Int
        run { setColorFilter(color) }
    }
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun ImageView.setColorStateListAnimator(
    @ColorInt color: Int,
    @ColorInt unselectedColor: Int
) {
    val stateList = StateListAnimator().apply {
        addState(
            intArrayOf(android.R.attr.state_selected),
            colorAnimator(unselectedColor, color, 350)
        )
        addState(
            intArrayOf(),
            colorAnimator(color, unselectedColor, 350)
        )
    }

    stateListAnimator = stateList

    // Refresh the drawable state to avoid the unselected animation on view creation
    refreshDrawableState()
}


/**
 * Выводим во view изображение из сети или локального хранилища
 */
fun ImageView.loadImage(url: Any) {
    val isSvg = getFileExtension(url.toString()) == FileConstant.SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(SvgSoftwareLayerSetter())
            .load(Uri.parse(url.toString()))
    } else {
        Glide.with(this.context)
            .load(url)

    }
    request.centerCrop().into(this)
}


/**
 * Load image with cache
 */
fun ImageView.loadImageWithCache(url: Any) {
    val isSvg = getFileExtension(url.toString()) == FileConstant.SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(SvgSoftwareLayerSetter())
            .load(Uri.parse(url.toString()))
    } else {
        Glide.with(this.context).load(url)

    }
    request.diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
}


/**
 * Выводим во view без кропа изображение из сети или локального хранилища
 */
fun ImageView.loadImageWithoutCrop(url: Any) {
    val isSvg = getFileExtension(url.toString()) == FileConstant.SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(SvgSoftwareLayerSetter())
            .load(Uri.parse(url.toString()))
    } else {
        Glide.with(this.context)
            .load(url)
    }
    request.into(this)
}


/**
 * Load image with cache and success/fail listeners
 */
fun ImageView.loadImageWithCacheAndListener(
    url: Any,
    readyBlock: (Drawable?) -> Unit,
    failBlock: (Boolean) -> Unit
) {
    val isSvg = getFileExtension(url.toString()) == FileConstant.SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(SvgSoftwareLayerSetter(readyBlock, failBlock))
            .load(Uri.parse(url.toString()))
    } else {
        Glide.with(this.context)
            .load(url)
            .listener(ImgSoftwareLayer(readyBlock, failBlock))

    }
    request.diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
}


/**
 * Load image with cache and success/fail listeners
 */
fun ImageView.loadImageCenterCropWithCacheAndListener(
    url: Any,
    readyBlock: (Drawable?) -> Unit,
    failBlock: (Boolean) -> Unit
) {
    val isSvg = getFileExtension(url.toString()) == FileConstant.SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(SvgSoftwareLayerSetter(readyBlock, failBlock))
            .load(Uri.parse(url.toString()))
            .centerCrop()
    } else {
        Glide.with(this.context)
            .load(url)
            .centerCrop()
            .listener(ImgSoftwareLayer(readyBlock, failBlock))

    }
    request.diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(this)
}