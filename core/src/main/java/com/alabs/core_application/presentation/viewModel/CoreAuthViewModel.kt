package com.alabs.core_application.presentation.viewModel

import com.alabs.core_application.domain.auth.useCase.CoreCheckAuthUserUseCase
import com.alabs.core_application.domain.auth.useCase.CoreIsPendingAuthorizationPassedUseCase

/**
 * Всею общую информация для авторизации нужно переность сюда
 */
open class CoreAuthViewModel(
    private val isAuthUserUseCase: CoreCheckAuthUserUseCase,
    private val isPendingAuthorizationPassedUseCase: CoreIsPendingAuthorizationPassedUseCase
) : CoreLaunchViewModel() {

    val isPendingAuthorizationPassed: Boolean
        get() = isAuthUserUseCase.execute().isAuthUser && isPendingAuthorizationPassedUseCase.execute()

}