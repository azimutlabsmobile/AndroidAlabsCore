package com.alabs.core_application.data.services

import android.app.Application
import android.provider.ContactsContract
import com.alabs.core_application.data.models.ContactsPhoneBook


/**
 * Сервис по по получению контактных данных
 */
class ContactService(private val application: Application) {

    /**
     * Получение списка контактов в телефонной книге
     * ВНИМАНИЕ!!! ВЫПОЛНЯТЬ ОПЕРАЦИЮ АСИНХРОННО
     */
    fun getAllContact(): List<ContactsPhoneBook> {
        val contactList = mutableListOf<ContactsPhoneBook>()

        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )
        val phones = application.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        ) ?: return emptyList()

        if (phones.count > 0) {
            while (phones.moveToNext()) {
                val name: String =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                        .orEmpty()
                val phoneNumber: String =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .orEmpty()
                val photoUri =
                    phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                        .orEmpty()

                contactList.add(
                    ContactsPhoneBook(
                        name = name,
                        phoneNumber = phoneNumber,
                        avatar = photoUri
                    )
                )

            }
        }
        phones.close()
        return contactList
    }
}
