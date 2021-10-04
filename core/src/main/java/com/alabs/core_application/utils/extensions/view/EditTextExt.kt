package com.alabs.core_application.utils.extensions.view

import android.os.CountDownTimer
import android.text.InputFilter
import android.widget.EditText
import com.alabs.core_application.data.constants.CorePatternConstant
import com.alabs.core_application.presentation.ui.widget.editText.textWatcher.PhoneNumberTextWatcher
import com.alabs.core_application.presentation.ui.widget.editText.textWatcher.amount.AmountWithZeroTextWatcher

/**
 * Случатель текстового поля с формированием числового значения
 */
fun EditText.amountWithZeroDoOnTextChange(
    pattern: String = CorePatternConstant.PATTERN_FORMAT_AMOUNT_SPACE_ZERO,
    block: (String) -> Unit
) {
    this.addTextChangedListener(
        AmountWithZeroTextWatcher(pattern, this
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
 * Задаем максимольную длинну символов текстового поля
 * @param value значение
 */
fun EditText.setMaxLength(value: Int) {
    val fArray = arrayOfNulls<InputFilter>(1)
    fArray[0] = InputFilter.LengthFilter(value)
    filters = fArray
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