package com.alabs.core_application.utils.os

import android.net.Uri
import android.webkit.MimeTypeMap
import com.alabs.core_application.data.constants.CoreConstant
import com.alabs.core_application.data.constants.FileConstant.AUTHORITY
import java.io.File
import java.text.DecimalFormat

/**
 * Получение расширения файла
 */
fun getExtension(uri: String?): String {
    if (uri == null) {
        return CoreConstant.EMPTY
    }
    val dot = uri.lastIndexOf(".")
    return if (dot >= 0) {
        uri.substring(dot)
    } else {
        CoreConstant.EMPTY
    }
}

/**
 * Проверка лежил ли файл локально
 */
fun isLocal(url: String?) = url != null && !url.startsWith("http://") && !url.startsWith("https://")

/**
 * Проверка находиться ли файл в медиа сторе
 */
fun isMediaUri(uri: Uri?) = "media".equals(uri?.authority, ignoreCase = true)

/**
 * Конфертируем файл в uri
 */
fun getUri(file: File?): Uri? {
    return if (file != null) {
        Uri.fromFile(file)
    } else null
}

/**
 * Получение пути без имени файла
 */
fun getPathWithoutFilename(file: File?): File? {
    return if (file != null) {
        if (file.isDirectory) {
            file
        } else {
            val filename = file.name
            val filepath = file.absolutePath
            var pathwithoutname = filepath.substring(
                0,
                filepath.length - filename.length
            )
            if (pathwithoutname.endsWith("/")) {
                pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
            }
            File(pathwithoutname)
        }
    } else null
}

/**
 * Получение MIME type по файлу
 */
fun getMimeType(file: File): String? {
    val extension =
        getExtension(file.name)
    return if ((extension?.length) ?: 0 > 0) MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension?.substring(1)) else "application/octet-stream"
}

/**
 * Проверка файла на существование в ExternalStorage
 */
fun isExternalStorageDocument(uri: Uri) = "com.android.externalstorage.documents" == uri.authority

/**
 * Проверка файла на существование в LocalStorag
 */
fun isLocalStorageDocument(uri: Uri) = AUTHORITY == uri.authority

/**
 * Проверка файла на существование в загрузках
 */
fun isDownloadsDocument(uri: Uri) = "com.android.providers.downloads.documents" == uri.authority

/**
 * Проверка файла на существование в google фото
 */
fun isGooglePhotosUri(uri: Uri) = "com.google.android.apps.photos.content" == uri.authority

/**
 * Проверка файла на существование в медийных документах
 */
fun isMediaDocument(uri: Uri) = "com.android.providers.media.documents" == uri.authority

/**
 * Получите размер файла в удобочитаемой строке.
 */
fun getReadableFileSize(size: Int): String {
    val BYTES_IN_KILOBYTES = 1024
    val dec = DecimalFormat("###.#")
    val KILOBYTES = " KB"
    val MEGABYTES = " MB"
    val GIGABYTES = " GB"
    var fileSize = 0f
    var suffix = KILOBYTES
    if (size > BYTES_IN_KILOBYTES) {
        fileSize = size / BYTES_IN_KILOBYTES.toFloat()
        if (fileSize > BYTES_IN_KILOBYTES) {
            fileSize /= BYTES_IN_KILOBYTES
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize /= BYTES_IN_KILOBYTES
                suffix = GIGABYTES
            } else {
                suffix = MEGABYTES
            }
        }
    }
    return dec.format(fileSize.toDouble()) + suffix
}

