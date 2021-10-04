package com.alabs.core_application.utils.delegates

import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import com.alabs.core_application.data.constants.CoreConstant.MIN_VERSION_SUPPORT_DARK_MODE
import com.alabs.core_application.data.constants.CoreVariables
import com.alabs.core_application.data.prefs.SourcesLocalDataSource
import com.alabs.core_application.utils.os.getOSVersion
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.lang.RuntimeException


/**
 * Делегат служит для определения темы и смены темы приложения
 */
interface DarkTheme {

    /**
     * Получить тему приложения
     */
    fun initTheme(window: Window, defaultTheme: Theme)

    /**
     * Задать тему приложения для того чтобы задать тему используте Activity.setTheme
     */
    fun setTheme(theme: Theme, window: Window)

    /**
     * Получить тему приложения
     */
    fun getUITheme(): Theme

    /**
     * Проверка поддержки Dark mode
     */
    fun checkSupportDarkMode(): Boolean

}


class DarkThemeDelegate : DarkTheme, KoinComponent {


    private val sourcesLocalStorage by inject<SourcesLocalDataSource>()

    override fun initTheme(window: Window, defaultTheme: Theme) {
        val theme = sourcesLocalStorage.getTheme(Theme.SYSTEM.name) ?: defaultTheme.name
        makeTheme(theme, window)
    }

    override fun setTheme(theme: Theme, window: Window) {
        if (getOSVersion() < MIN_VERSION_SUPPORT_DARK_MODE && CoreVariables.IS_PRODUCTION) {
            throw RuntimeException("Устройство не поддерживает dark mode")
        }

        sourcesLocalStorage.setTheme(theme)
        makeTheme(sourcesLocalStorage.getTheme(Theme.SYSTEM.name), window)
    }

    override fun getUITheme(): Theme {
        return Theme.valueOf(sourcesLocalStorage.getTheme(Theme.SYSTEM.name).orEmpty())
    }

    override fun checkSupportDarkMode() = getOSVersion() >= MIN_VERSION_SUPPORT_DARK_MODE

    private fun makeTheme(theme: String?, window: Window) {
        if (getOSVersion() < MIN_VERSION_SUPPORT_DARK_MODE && CoreVariables.IS_PRODUCTION) {
            return
        }
        when (theme) {
            Theme.DARK.name -> makeDarkTheme(window)
            Theme.APP.name -> makeLightTheme(window)
            Theme.SYSTEM.name -> makeSystemTheme(window)
        }
    }

    private fun makeDarkTheme(window: Window) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        window.decorView.systemUiVisibility = 0
    }

    private fun makeLightTheme(window: Window) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun makeSystemTheme(window: Window) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

}

enum class Theme {
    DARK, APP, SYSTEM
}


