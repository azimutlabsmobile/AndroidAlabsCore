package com.alabs.core_application.utils.delegates

import com.alabs.core_application.data.models.ContactsPhoneBook
import com.alabs.core_application.utils.os.unmaskPhoneNumber

/**
 * Делегируем поиск по контактам
 */
interface SearchContact {

    /**
     * Поиск контактов в телефонной книге
     */
    fun searchContact(
        it: List<ContactsPhoneBook>?,
        phoneNumber: String?
    ): ContactsPhoneBook?

}

class SearchContactDelegate : SearchContact {
    override fun searchContact(
        it: List<ContactsPhoneBook>?,
        phoneNumber: String?
    ): ContactsPhoneBook? {
        return it?.find {
            unmaskPhoneNumber(it.phoneNumber) == unmaskPhoneNumber(phoneNumber)
        }
    }

}