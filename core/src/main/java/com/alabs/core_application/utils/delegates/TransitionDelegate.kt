package com.alabs.core_application.utils.delegates

import androidx.appcompat.app.AppCompatActivity
import com.alabs.core_application.R

/**
 * Анимируем окна при переходе
 */
interface TransitionAnimation {

    fun initTransition(activity: AppCompatActivity)

    /**
     * Анимируем окно с низу вверх
     */
    fun animBottomToTop()

    /**
     * Анимируем окно с верху в них
     */
    fun animTopToBottom()

    /**
     * Анимируем c лева на право
     */
    fun animLeftToRight()

    /**
     * Анимируем c права на лево
     */
    fun rightToLeft()

}


/**
 * Анимация при переходе activity
 */
class TransitionAnimationActivityDelegate : TransitionAnimation {

    private var mActivity: AppCompatActivity? = null

    override fun initTransition(activity: AppCompatActivity) {
        this.mActivity = activity
    }


    override fun animBottomToTop() {
        mActivity?.overridePendingTransition(
            R.anim.slide_enter_top,
            R.anim.slide_exit_top
        )
    }

    override fun animTopToBottom() {
        mActivity?.overridePendingTransition(
            R.anim.slide_enter_bottom,
            R.anim.slide_exit_bottom
        )
    }

    override fun animLeftToRight() {
        mActivity?.overridePendingTransition(
            R.anim.slide_enter_left,
            R.anim.slide_exit_left
        )
    }

    override fun rightToLeft() {
        mActivity?.overridePendingTransition(
            R.anim.slide_enter_rigth,
            R.anim.slide_exit_rigth
        )
    }

}

