package com.alabs.core_application.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Класс для обертки String в Parcelable
 * Используется для передачи строкового значения в Activity.showModuleActivity()
 */
@Parcelize
data class ParcelableString(
    val data: String
) : Parcelable