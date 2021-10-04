package com.alabs.core_application.data.constants

object FileConstant {
    const val MIME_TYPE_AUDIO = "audio/*"
    const val MIME_TYPE_TEXT = "text/*"
    const val MIME_TYPE_IMAGE = "image/*"
    const val MIME_TYPE_VIDEO = "video/*"
    const val MIME_TYPE_APP = "application/*"
    const val HIDDEN_PREFIX = "."
    const val MIME_TYPE_PDF = "application/pdf"
    const val AUTHORITY = "com.ianhanniballake.localstorage.documents"
    val MIME_TYPE_ALL_DOCUMENTS = arrayOf(
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "text/plain",
        "application/pdf",
        "application/zip"
    )
    const val SVG_FILE = ".svg"
}

