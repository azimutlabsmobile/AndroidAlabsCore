package com.alabs.core_application.data.repository

import com.alabs.core_application.data.prefs.SecurityDataSource

/**
 * Репозиторий для работы с авторизационными данными
 */
open class CoreAuthUserDateRepository(private val secureDataSource: SecurityDataSource) {

    /**
     * Сохраненение токена
     * @param accessToken токен
     */
    fun setAccessToken(accessToken: String) = secureDataSource.setAccessToken(accessToken)

    /**
     * Получение токена
     */
    fun getAccessToken() = secureDataSource.getAccessToken().orEmpty()

    /**
     * Сохраненение refresh токента
     */
    fun getRefreshToken() = secureDataSource.getRefreshToken().orEmpty()

    /**
     * Сохраненение refresh токена
     * @param refreshToken токен
     */
    fun setRefreshToken(refreshToken: String) = secureDataSource.setRefreshToken(refreshToken)

    /**
     * Получение номера телефона пользователя
     */
    fun getPhoneNumber() = secureDataSource.getPhoneNumber().orEmpty()

    /**
     * Запись номера телефона пользователя
     */
    fun setPhoneNumber(phoneNumber: String) = secureDataSource.setPhoneNumber(phoneNumber)

    /**
     * Получение пин кода
     */
    fun getPinCode() = secureDataSource.getAccessPinCode()

    /**
     * Сохраненение пин кода токена
     * @param pinCode пин код
     */
    fun savePinCode(pinCode: String) = secureDataSource.setAccessPinCode(pinCode)


    /**
     * Сохранить имя пользователя
     * @param userName имя пользователя
     */
    fun setUserName(userName : String) = secureDataSource.setUserName(userName)

    /**
     * Получить имя пользователя
     */
    fun getUserName() = secureDataSource.getUserName()

    /**
     * Сохранить аватар пользователя
     * @param avatar имя пользователя
     */
    fun setUserAvatar(avatar : String) = secureDataSource.setUserAvatar(avatar)

    /**
     * Получить аватар пользователя
     */
    fun getUserAvatar() = secureDataSource.getUserAvatar()

    /**
     * Удалить аватар пользователя
     */
    fun removeUserAvatar() = secureDataSource.removeUserAvatar()

    /*
   * Проверка авторизован ли ожидающий подтверждения авторизации пользователь
   */
    fun isPendingAuthorizationPassed() = secureDataSource.isAuthPendingUserAuthorized

    /*
    * Сохраняем стейт что пользователь ожидающий авторизации потвердился
    */
    fun setPendingAuthorizationPassed() {
        secureDataSource.isAuthPendingUserAuthorized = true
    }

    /*
    * Сбрасываем стейт что пользователь ожидающий авторизации потвердился
    */
    fun resetPendingAuthorizationPassed() {
        secureDataSource.isAuthPendingUserAuthorized = false
    }

}