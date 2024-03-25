package com.example.program0201roomdb

sealed interface ContactEvent {
    object SaveContact: ContactEvent
    data class SetFirstName(val firstName: String): ContactEvent
    data class SetLastName(val lastName: String): ContactEvent
    data class SetPhoneNumber(val phoneNumber: String): ContactEvent
    object ShowDialogue: ContactEvent
    object HideDialogue: ContactEvent
    data class SortContacts(val sortType: SortType): ContactEvent
    data class DeleteContact(val contact: Contact): ContactEvent
}