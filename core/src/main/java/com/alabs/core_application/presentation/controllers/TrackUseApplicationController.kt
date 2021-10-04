package com.alabs.core_application.presentation.controllers

import android.os.CountDownTimer
import com.alabs.core_application.data.prefs.SourcesLocalDataSource
import com.alabs.core_application.presentation.ui.activities.CoreAuthorizedActivity
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.ref.WeakReference

/**
 * Используеться для отслеживания активности пользователя
 * Для чего используеться
 * 1. Если пользователь бездействует во время открытого приложения и в тоже время авторизован
 * то по истечению определенного времени перекидываем пользователя на экран авторизации
 * 2. Если пользователь свирнул приложение и прошло после окрытия больше заданного времени
 * то после открытия перекидывыаем на экран логина
 */
interface TrackUseApplication {

    /**
     * Локальное время сессии
     */
    var timeSession: Long

    var isAuthUser : Boolean

    /**
     * Даем понять контроллеру что пользователь использует приложение
     */
    fun onTouchEvent()

    /**
     * Даем понять контроллеру что пользователь начал пользоваться приложением
     */
    fun onStartTrack(activity: CoreAuthorizedActivity)

    /**
     * Даем понять контроллеру что пользователь вернулся в приложение
     */
    fun onResumeTrack()

    /**
     * Даем понять контролеру что пользователь свернул приложение
     */
    fun onPauseTrack()

    /**
     * Разрушаем activity для избежания утечки памяти
     */
    fun onDestroyTrack()
}

/**
 * @param isUseLocalSession нужно ли использовать локальную сессию
 */
class TrackUseApplicationController(private val isUseLocalSession: Boolean) :
    TrackUseApplication,
    KoinComponent {

    private val sourceDataPref by inject<SourcesLocalDataSource>()

    private var mActivity: WeakReference<CoreAuthorizedActivity>? = null
    private var countDownTimer: CountDownTimer? = null
    private var isActiveApp = true




    override var timeSession: Long = -1L
        set(value) {
            field = value * 60 * 1000
        }

    override var isAuthUser: Boolean = false


    override fun onStartTrack(activity: CoreAuthorizedActivity) {
        mActivity = WeakReference(activity)
        if(checkEndLocalSession()){
            redirectLoginInCaseOfInaction()
            return
        }
        startTimer()
    }

    private fun createCountDownTimer(): CountDownTimer {
        return object : CountDownTimer(timeSession, 1000) {
            override fun onFinish() {
                redirectLoginInCaseOfInaction()
            }

            override fun onTick(p0: Long) {
                // do nothing
            }
        }
    }

    override fun onTouchEvent() {
        sourceDataPref.setLastTimeUseApplication(System.currentTimeMillis())
        stopTimer()
        startTimer()
    }

    private fun startTimer() {
        isActiveApp = true
        if (isUseLocalSession && isActiveApp && isAuthUser) {
            sourceDataPref.removeLastTimeUseApplication()
            countDownTimer = createCountDownTimer().start()
        }

    }

    private fun stopTimer() {
        if (isUseLocalSession && isActiveApp && isAuthUser) {
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    override fun onDestroyTrack() {
        mActivity?.clear()
        mActivity = null
        if (isUseLocalSession && isActiveApp && isAuthUser) {
            sourceDataPref.removeLastTimeUseApplication()
        }
    }

    override fun onResumeTrack() {
        if(checkEndLocalSession()){
            redirectLoginInCaseOfInaction()
            return
        }
        stopTimer()
        startTimer()
    }

    override fun onPauseTrack() {
        sourceDataPref.setLastTimeUseApplication(System.currentTimeMillis())
        stopTimer()
    }

    private fun redirectLoginInCaseOfInaction() {
        sourceDataPref.removeLastTimeUseApplication()
        mActivity?.get()?.redirectLoginInCaseOfInaction()
        isActiveApp = false
    }

    private fun checkEndLocalSession() : Boolean{
        if (isUseLocalSession && isActiveApp && isAuthUser) {
            if ((System.currentTimeMillis() - timeSession) >= sourceDataPref.getLastTimeUseApplication() &&
                sourceDataPref.getLastTimeUseApplication() != 0L
            ) {
               return true
            }
        }

        return false

    }
}