package com.alabs.core_application.utils.callback

interface PermissionHandler {

    fun confirmPermission() {
        // do nothing
    }


    fun confirmWithRequestCode(requestCode: Int) {
        // do nothing
    }


    fun ignorePermission() {
        // do nothing
    }
}