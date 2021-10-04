package com.alabs.core_application.data.models

import android.os.Parcelable
import com.alabs.multiAdapter.SearchMultitype
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactsPhoneBook(
    val name: String,
    val phoneNumber: String,
    val avatar: String
): SearchMultitype(phoneNumber, name), Parcelable