package com.alabs.core_application.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactsPhoneBook(
    val name: String,
    val phoneNumber: String,
    val avatar: String
) : Parcelable