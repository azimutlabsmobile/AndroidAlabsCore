package com.alabs.core_application.utils.permission

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * Использовать данный делегат для запроса на разрешение какого-либо типа
 */
interface Permission {

    /**
     *  Запрашивает все разрешения указанные в массиве
     */
    fun permissionExistFragment(
        arrayPermission: Array<String>,
        request: Int
    ): Boolean

    /**
     * Убивает ресурсы Activity/Fragment
     */
    fun dispose()
}

/**
 * Реализация на запрос различных разрешений
 * ОБЯЗАТЕЛЬНО в [activity] в методе onDestroy вызвать [dispose] во избежании утечки памяти
 */
class PermissionImpl(
    private var fragment: Fragment? = null
) : Permission {

    override fun permissionExistFragment(
        arrayPermission: Array<String>,
        request: Int
    ): Boolean {
        fragment?.context?.let {
            val activity = fragment?.activity ?: return false
            arrayPermission.forEach { permission ->
                return if (ContextCompat.checkSelfPermission(
                        it,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission(arrayPermission, request, activity)
                    false
                } else {
                    true
                }
            }
        }
        return true
    }

    private fun requestPermission(
        arrayPermission: Array<String>,
        request: Int,
        activity: FragmentActivity
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fragment?.requestPermissions(
                arrayPermission,
                request
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayPermission,
                request
            )
        }
    }

    override fun dispose() {
        fragment = null
    }
}

class ActivityaPermissionImpl(
    private var activity: AppCompatActivity?
) : Permission {

    override fun permissionExistFragment(
        arrayPermission: Array<String>,
        request: Int
    ): Boolean {

        arrayPermission.forEach { permission ->
            activity?.let {
                return if (ContextCompat.checkSelfPermission(
                        it,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission(arrayPermission, request, it)
                    false
                } else {
                    true
                }
            }
        }
        return true
    }

    private fun requestPermission(
        arrayPermission: Array<String>,
        request: Int,
        activity: FragmentActivity
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(arrayPermission, request)
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayPermission,
                request
            )
        }
    }

    override fun dispose() {
        activity = null
    }
}

