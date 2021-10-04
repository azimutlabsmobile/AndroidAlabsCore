package com.alabs.core_application.utils.extensions

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.StateListAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.NestedScrollView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.alabs.core_application.data.constants.CorePatternConstant
import com.alabs.core_application.data.constants.FileConstant.SVG_FILE
import com.alabs.core_application.presentation.model.UIDrawableClick
import com.alabs.core_application.presentation.ui.glide.ImgSoftwareLayer
import com.alabs.core_application.presentation.ui.glide.SvgSoftwareLayerSetter
import com.alabs.core_application.presentation.ui.widget.editText.textWatcher.PhoneNumberTextWatcher
import com.alabs.core_application.presentation.ui.widget.editText.textWatcher.amount.AmountWithZeroTextWatcher
import com.alabs.core_application.utils.os.getExtension

/**
 * Created by Yergali Zhakhan on 5/20/20.
 */

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
 * Случатель текстового поля с формированием числового значения
 */
fun EditText.amountWithZeroDoOnTextChange(
    patten: String = CorePatternConstant.PATTERN_FORMAT_ANOUNT_SPACE_ZERO,
    block: (String) -> Unit
) {
    this.addTextChangedListener(
        AmountWithZeroTextWatcher(
            patten,
            this
        ) {
            block(it)
        })
}


/**
 * Случатель текстового поля с формированием числового значения
 */
fun EditText.phoneNumberDoOnTextChange(
    block: (String) -> Unit,
    mask: String = CorePatternConstant.PATTERN_DEFAULT_PHONE_NUMBER
) {
    this.addTextChangedListener(PhoneNumberTextWatcher(this, mask) {
        block(it)
    })
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
 * Слушатель скролла viewPager
 */
fun ViewPager.scrollListener(block: (Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            block(position)
        }
    })
}


/**
 * Слушатель для TabLayout
 * @return block возращает выбранный таб
 */
fun TabLayout.selectedListener(block: ((TabLayout.Tab?) -> Unit)) {
    addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {
        override fun onTabReselected(tab: TabLayout.Tab?) {
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabSelected(tab: TabLayout.Tab?) {
            block(tab)
        }
    })
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
 * Выводим во view изображение из сети или локального хранилища
 */
fun ImageView.loadImage(url: Any) {

    val isSvg = getExtension(url.toString()) == SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(withCrossFade())
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
    val isSvg = getExtension(url.toString()) == SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(withCrossFade())
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
    val isSvg = getExtension(url.toString()) == SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(withCrossFade())
            .listener(SvgSoftwareLayerSetter())
            .load(Uri.parse(url.toString()))
    } else {
        Glide.with(this.context)
            .load(url)
    }
    request.into(this)
}

/**
 * Получаем drawable из изображение по url
 * @param path путь до изображения
 * @param block лямбла каторая возращает Drawable
 *
 */
fun Context.getDrawableUrlImage(path: Any, block: (Drawable) -> Unit) {

    Glide.with(this).`as`(Bitmap::class.java)
        .load(path)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                block(BitmapDrawable(resources, resource))
            }

            override fun onLoadCleared(placeholder: Drawable?) {
            }
        })


}

/**
 * Load image with cache and success/fail listeners
 */
fun ImageView.loadImageWitCacheAndListener(
    url: Any,
    readyBlock: (Drawable?) -> Unit,
    failBlock: (Boolean) -> Unit
) {
    val isSvg = getExtension(url.toString()) == SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(withCrossFade())
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
fun ImageView.loadImageCenterCropWitCacheAndListener(
    url: Any,
    readyBlock: (Drawable?) -> Unit,
    failBlock: (Boolean) -> Unit
) {
    val isSvg = getExtension(url.toString()) == SVG_FILE
    val request = if (isSvg) {
        Glide.with(this.context).`as`(Drawable::class.java)
            .transition(withCrossFade())
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

/**
 * Делаем view неактивной
 * @param isViewEnabled true view активная false неактивная
 */
fun View.setStateEnable(isViewEnabled: Boolean) {
    this.isClickable = isViewEnabled
    this.isEnabled = isViewEnabled
    this.alpha = if (isViewEnabled) 1.0f else 0.4f
}

/**
 * Слушатель кликов для иконки
 */
fun TextView.drawableClickListener(drawablePosition: UIDrawableClick, block: ((View) -> Unit)) {

    val padding = when (drawablePosition) {
        UIDrawableClick.TOP -> totalPaddingTop
        UIDrawableClick.LEFT -> totalPaddingStart
        UIDrawableClick.RIGHT -> totalPaddingEnd
        UIDrawableClick.BOTTOM -> totalPaddingBottom

    }

    setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= right - padding) {
                block(this)
            } else {
                requestFocus()
                context.showKeyboard(this)
            }
        }
        true
    }
}

/**
 * Задаем максимольную длинну символов текстового поля
 * @param value значение
 */
fun EditText.setMaxLength(value: Int) {
    val fArray = arrayOfNulls<InputFilter>(1)
    fArray[0] = InputFilter.LengthFilter(value)
    filters = fArray
}


/**
 * Подчеркиваем TextView
 */
fun TextView.underline() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}


/**
 * Слушатель NestedScrollView срабатывает когда сколл доходит до конца страници
 */
fun NestedScrollView.doOnEndPage(block: () -> Unit) {
    this.setOnScrollChangeListener { v: NestedScrollView, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
        val view = v.getChildAt(v.childCount - 1) as View
        val diff: Int = view.bottom - (v.height + v.scrollY)
        if (diff == 0) {
            block()
        }
    }
}


/**
 * Функция приостанавливает передачу данных введенного в текстовое поле на определенное время
 * @param delay время задержки
 * @param bloc результат
 */
fun EditText.delayDoOnTextChange(delay: Long = 400, bloc: (String) -> Unit) {
    var countDownTimer: CountDownTimer? = null
    doOnTextChange { value ->
        countDownTimer?.cancel()
        countDownTimer = null
        countDownTimer = object : CountDownTimer(delay, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // do nothing
            }
            override fun onFinish() {
                bloc(value?.toString().orEmpty())
            }
        }
        countDownTimer?.start()
        Unit
    }

}