package com.alabs.core_application.presentation.ui.widget.clickListeners

import android.os.SystemClock
import android.view.View

class SafeClickListener(
    private var defaultInterval: Int = 1700,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}
