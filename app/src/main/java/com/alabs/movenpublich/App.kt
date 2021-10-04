package com.alabs.movenpublich

import android.content.pm.ActivityInfo
import androidx.multidex.MultiDexApplication
import com.alabs.core_application.di.getCoreDIModule
import com.alabs.core_application.utils.extensions.coreBuilder

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        coreBuilder {
            koinModule {
                getCoreDIModule()
            }
            allActivitiesOrientation {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }
}