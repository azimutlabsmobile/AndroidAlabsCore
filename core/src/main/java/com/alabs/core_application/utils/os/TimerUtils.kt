package com.alabs.core_application.utils.os

import com.alabs.core_application.utils.wrappers.CountDownTimerWrapper

/**
 * Таймер обратного отчета в секундах
 * @param second сколько секунд для обртаного отчета
 * @param finish block лямбды для при остановку таймера
 * @param tick block лямбды при работе таймера
 */
fun secondCountDownTimer(
    second: Long = 10000,
    finish: () -> Any,
    tick: (Long) -> Any
): CountDownTimerWrapper = object : CountDownTimerWrapper(second, 1000) {

    override fun onFinish() {
        super.onFinish()
        finish()
    }

    override fun onTick(p0: Long) {
        super.onTick(p0)
        val time = p0 / 1000
        tick(time)

    }
}


