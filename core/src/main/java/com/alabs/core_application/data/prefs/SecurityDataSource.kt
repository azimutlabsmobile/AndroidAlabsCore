package com.alabs.core_application.data.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.alabs.core_application.data.constants.CoreConstant.EMPTY
import com.alabs.core_application.data.constants.CoreConstant.PREF_ACCESS_PIN_CODE
import com.alabs.core_application.data.constants.CoreConstant.PREF_ACCESS_TEMP_PIN_CODE
import com.alabs.core_application.data.constants.CoreConstant.PREF_AUTH_ACCESS_TOKEN
import com.alabs.core_application.data.constants.CoreConstant.PREF_AUTH_REFRESH_TOKEN
import com.alabs.core_application.data.constants.CoreConstant.PREF_AUTH_SESSION
import com.alabs.core_application.data.constants.CoreConstant.PREF_IS_USE_FACE_ID
import com.alabs.core_application.data.constants.CoreConstant.PREF_IS_USE_FINGER_PRINT
import com.alabs.core_application.data.constants.CoreConstant.PREF_PHONE_NUMBER
import com.alabs.core_application.data.constants.CoreConstant.PREF_USER_AVATAR
import com.alabs.core_application.data.constants.CoreConstant.PREF_USER_NAME

/**
 * Возможность хранить защишенные данные (токен, пароль, логин)
 */
class SecurityDataSource(private val pref: SharedPreferences) {

    /*
    * При каждом создании приложения по умолчанию isAuthPendingUser = false,
    * так как изначально все пользователи не авторизованы и ждут подтверждения
    */
    var isAuthPendingUserAuthorized = false

    /**
     * Делаем logout пользователя
     */
    fun clearAuthorizedUserData() {
        pref.edit { remove(PREF_ACCESS_PIN_CODE) }
        pref.edit { remove(PREF_AUTH_SESSION) }
        pref.edit { remove(PREF_AUTH_ACCESS_TOKEN) }
        pref.edit { remove(PREF_AUTH_REFRESH_TOKEN) }
        pref.edit { remove(PREF_USER_NAME) }
        pref.edit { remove(PREF_USER_AVATAR) }
    }

    /**
     * Сохранить пин код
     */
    fun setAccessPinCode(pinCode: String) = pref.edit { putString(PREF_ACCESS_PIN_CODE, pinCode) }

    /**
     * Получить пин код
     */
    fun getAccessPinCode() = pref.getString(PREF_ACCESS_PIN_CODE, EMPTY)

    /**
     * Удалить пин код
     */
    fun clearAccessPinCode() = pref.edit { remove(PREF_ACCESS_PIN_CODE) }

    /**
     * Сохранить временный пин код
     */
    fun setAccessTempPinCode(pinCode: String) =
        pref.edit { putString(PREF_ACCESS_TEMP_PIN_CODE, pinCode) }

    /**
     * Получить временный пин код
     */
    fun getAccessTempPinCode() = pref.getString(PREF_ACCESS_TEMP_PIN_CODE, EMPTY)

    /**
     * Удалить временный пин код
     */
    fun cleanTempCode() = pref.edit { remove(PREF_ACCESS_TEMP_PIN_CODE) }

    /**
     * Сохранить auth access token
     */
    fun setAccessToken(token: String) = pref.edit { putString(PREF_AUTH_ACCESS_TOKEN, token) }

    /**
     * Получить auth access token
     */
    fun getAccessToken() = pref.getString(PREF_AUTH_ACCESS_TOKEN, EMPTY)

    /**
     * Сохранить auth refresh token
     */
    fun setRefreshToken(token: String) = pref.edit { putString(PREF_AUTH_REFRESH_TOKEN, token) }

    /**
     * Получить auth refresh token
     */
    fun getRefreshToken() = pref.getString(PREF_AUTH_REFRESH_TOKEN, EMPTY)

    /**
     * Сохранить auth session
     */
    fun setSession(session: String) = pref.edit { putString(PREF_AUTH_SESSION, session) }


    /**
     * Получить имя пользователя
     */
    fun getUserName() = pref.getString(PREF_USER_NAME, EMPTY)

    /**
     * Сохранить имя пользователя
     */
    fun setUserName(userName: String) = pref.edit { putString(PREF_USER_NAME, userName) }

    /**
     * Получить аватар пользователя
     */
    fun getUserAvatar() = pref.getString(PREF_USER_AVATAR, null)

    /**
     * Удаление аватара пользователя
     */
    fun removeUserAvatar() = pref.edit {
        remove(PREF_USER_AVATAR)
    }

    /**
     * Сохранить аватар пользователя
     */
    fun setUserAvatar(avatar: String) = pref.edit { putString(PREF_USER_AVATAR, avatar) }

    /**
     * Получить auth session
     */
    fun getSession() = pref.getString(PREF_AUTH_SESSION, EMPTY)

    /**
     * Сохранить phoneNumber
     */
    fun setPhoneNumber(phoneNumber: String) =
        pref.edit { putString(PREF_PHONE_NUMBER, phoneNumber) }

    /**
     * Получить phoneNumber
     */
    fun getPhoneNumber() = pref.getString(PREF_PHONE_NUMBER, EMPTY)


    /**
     * Задаем если необходимо использовать fingerPrint
     */
    fun setUseFingerPrint(isUseFingerPrint: Boolean) =
        pref.edit { putBoolean(PREF_IS_USE_FINGER_PRINT, isUseFingerPrint) }

    /**
     * Задаем если необходимо использовать faceId
     */
    fun setUseFaceId(isUseFingerPrint: Boolean) =
        pref.edit { putBoolean(PREF_IS_USE_FACE_ID, isUseFingerPrint) }

    /**
     * Проверка используеться ли отпечаток пальца
     */
    fun isUseFingerPrint() =
        pref.getBoolean(PREF_IS_USE_FINGER_PRINT, false)

    /**
     * Проверка используеться ли FaceId
     */
    fun isUseFaceIdPrint() = pref.getBoolean(PREF_IS_USE_FACE_ID, false)

    /**
     * Очищаем fingerPrint
     */
    fun clearFingerPrint() = pref.edit { remove(PREF_IS_USE_FINGER_PRINT) }


    /**
     * Очищаем faceId
     */
    fun clearFaceId() = pref.edit { remove(PREF_IS_USE_FACE_ID) }

}