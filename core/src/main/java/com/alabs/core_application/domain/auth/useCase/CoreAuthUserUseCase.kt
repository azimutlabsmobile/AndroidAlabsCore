package com.alabs.core_application.domain.auth.useCase

import com.alabs.core_application.data.repository.CoreAuthUserDateRepository
import com.alabs.core_application.domain.BaseNoneParamUseCase
import com.alabs.core_application.domain.auth.model.CheckUserAuthResult

/**
 * Проверка автиризован ли пользователь
 */
open class CoreCheckAuthUserUseCase(private val repos: CoreAuthUserDateRepository) :
    BaseNoneParamUseCase<CheckUserAuthResult> {

    override fun execute() = CheckUserAuthResult(isAuthUser = repos.getAccessToken().isNotEmpty())

}

/**
 * Получение номера телефона
 */
class CoreGetPhoneNumberUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.getPhoneNumber()
}

/**
 * Сохранение номера телефона
 */
class CoreSavePhoneNumberUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute(phoneNumber: String) = repos.setPhoneNumber(phoneNumber)
}

/**
 * Получаем access token
 */
class CoreGetAccessTokenUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.getAccessToken()
}

/**
 * Получаем access token
 */
class CoreGetRefreshTokenUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.getRefreshToken()
}


/**
 * Получаем имя пользователя
 */
class CoreGetNameUserUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.getUserName()
}


/**
 * Сохраняем имя пользоватля имя пользователя
 */
class CoreSetNameUserUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute(userName: String) = repos.setUserName(userName)

}

/**
 * Получаем аватар пользователя
 */
class CoreGetUserAvatarUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.getUserAvatar()
}


/**
 * Сохраняем аватар пользоватля
 */
class CoreSetUserAvatarUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute(avatar: String) = repos.setUserAvatar(avatar)

}


/**
 * Удаляем аватар пользоватля
 */
class CoreRemoveUserAvatarUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.removeUserAvatar()
}

/**
 * Проверка авторизован ли ожидающий подтверждения авторизации пользователь
 */
class CoreIsPendingAuthorizationPassedUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.getAccessToken().isNotEmpty() && repos.isPendingAuthorizationPassed()

}

/**
 * Сохраняем стейт что пользователь ожидающий авторизации потвердился
 */
class CoreSetPendingAuthorizationPassedUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.setPendingAuthorizationPassed()

}

/**
 * Сбрасываем стейт что пользователь ожидающий авторизации потвердился
 */
class CoreResetPendingAuthorizationPassedUseCase(private val repos: CoreAuthUserDateRepository) {

    fun execute() = repos.resetPendingAuthorizationPassed()

}