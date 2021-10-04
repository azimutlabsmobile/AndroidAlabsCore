package com.alabs.core_application.utils.wrappers

import android.os.CountDownTimer


/**
 * Обвертка над [CountDownTimer] с возможностью отслеживать запушен ли таймер
 */
open class CountDownTimerWrapper(
    millisInFuture: Long,
    countDownInterval: Long
) : CountDownTimer(millisInFuture, countDownInterval) {

    var isStart = false

    fun startTimer(): CountDownTimerWrapper {
        if (!isStart)
            start()
        return this
    }


    override fun onFinish() {
        isStart = false
    }

    override fun onTick(p0: Long) {
        isStart = true
    }

}