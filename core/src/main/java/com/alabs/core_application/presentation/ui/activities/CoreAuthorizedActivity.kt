package com.alabs.core_application.presentation.ui.activities

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.os.bundleOf
import com.alabs.core_application.data.constants.CoreConstant.ARG_IS_AUTH_FROM_INACTION
import com.alabs.core_application.data.constants.CoreVariables
import com.alabs.core_application.presentation.controllers.TrackUseApplication
import com.alabs.core_application.presentation.controllers.TrackUseApplicationController
import com.alabs.core_application.presentation.viewModel.CoreAuthViewModel
import com.alabs.core_application.utils.extensions.showActivityAndClearBackStack
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Использовать в авторизованой зоне
 * @param lay layout
 * @param isUseLocalSession если нужно использовать локальную сессию
 */
abstract class CoreAuthorizedActivity(
    lay: Int,
    isUseLocalSession: Boolean = true,
    private val isAuthCallBack: Boolean = false
) :
    CoreActivity(lay, isAuthCallBack),
    TrackUseApplication by TrackUseApplicationController(isUseLocalSession) {

    private val viewModel by viewModel<CoreAuthViewModel>()

    override fun redirectLogin() = showActivityAndClearBackStack(CoreVariables.LOGIN_ACTIVITY)


    override fun onCreate(savedInstanceState: Bundle?) {
        onStartTrack(this)
        super.onCreate(savedInstanceState)
    }

    /**
     * Выволнения кода при срабатывании бездействия
     */
    open fun redirectLoginInCaseOfInaction() {
        showActivityAndClearBackStack(CoreVariables.LOGIN_ACTIVITY, bundleOf(ARG_IS_AUTH_FROM_INACTION to true))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        onTouchEvent()
        return super.dispatchTouchEvent(ev)
    }


    override fun onResume() {
        super.onResume()
        onResumeTrack()
    }

    override fun onPause() {
        super.onPause()
        onPauseTrack()
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyTrack()
    }
}