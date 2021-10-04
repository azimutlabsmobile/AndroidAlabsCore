package com.alabs.core_application.utils.extensions

import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.alabs.core_application.data.constants.FileConstant
import com.alabs.core_application.data.constants.RequestCodeConstants
import com.alabs.core_application.data.constants.RequestCodeConstants.PICK_ALL_FILE


/**
 * Безопасное закрытие фрагмента, так как dismiss может вызвать падение приложения
 */
fun DialogFragment.safeDismiss() {
    try {
        dismiss()
    } catch (ex: Exception) {
        Log.e("Dialog exception", ex.message.orEmpty())
    }
}

/**
 * Получение файла
 */
fun DialogFragment.pickImageFile() {
    val intent = Intent().apply {
        type = FileConstant.MIME_TYPE_IMAGE
        action = Intent.ACTION_GET_CONTENT
    }
    startActivityForResult(Intent.createChooser(intent, null), RequestCodeConstants.PICK_IMAGE)
}

/**
 * Выбор файлов с определенный mimiType
 * @param array массив mimiType
 */
fun DialogFragment.pickFile(array: Array<String>) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        intent.type = if (array.size == 1) array[0] else "*/*"
        if (array.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, array)
        }
    } else {
        var mimeTypesStr = ""
        for (mimeType in array) {
            mimeTypesStr += "$mimeType|"
        }
        intent.type = mimeTypesStr.substring(0, mimeTypesStr.length - 1)
    }
    startActivityForResult(
        Intent.createChooser(
            intent,
            null
        ), PICK_ALL_FILE
    )
}