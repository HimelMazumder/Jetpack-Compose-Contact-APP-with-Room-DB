package com.example.program0201roomdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(
    private val dao: ContactDao
): ViewModel() {
    // using compose state
    /*var sortType by mutableStateOf(SortType.FIRST_NAME)
        private set
    var contactState by mutableStateOf(ContactState())
        private set*/

    // using state flow
    private val _sortType = MutableStateFlow<SortType>(SortType.FIRST_NAME)
    @OptIn(ExperimentalCoroutinesApi::class)
    // stateIn : returns a stateFlow, runs in viewModelScope, becomes a cold flow
    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when(sortType) {
                SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
                SortType.LAST_NAME -> dao.getContactsOrderedByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val sortType = _sortType.asStateFlow()

    /*private val _contactState = MutableStateFlow<ContactState>(ContactState())
    val contactState = _contactState.asStateFlow()*/

    private val _state = MutableStateFlow(ContactState())
    val state = combine(_state, _sortType, _contacts) {state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent) {
        when(event) {
            is ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()) {
                    return
                }

                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )

                viewModelScope.launch {
                    dao.upsertContact(contact = contact)
                }

                _state.update {
                    it.copy(
                        isAddingContact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                }
            }
            is ContactEvent.SetFirstName -> {
                _state.update { it.copy(
                    firstName = event.firstName
                ) }
            }
            is ContactEvent.SetLastName -> {
                _state.update { it.copy(
                    lastName = event.lastName
                ) }
            }
            is ContactEvent.SetPhoneNumber -> {
                /*we are copying, so that reference to the object is changed due to member value
                change, thus changing the an object state*/
                _state.update { it.copy(
                    phoneNumber = event.phoneNumber
                ) }
            }
            is ContactEvent.ShowDialogue -> {
                _state.update { it.copy(
                    isAddingContact = true
                ) }
            }
            is ContactEvent.HideDialogue -> {
                _state.update { it.copy(
                    isAddingContact = false
                ) }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
        }
    }

}