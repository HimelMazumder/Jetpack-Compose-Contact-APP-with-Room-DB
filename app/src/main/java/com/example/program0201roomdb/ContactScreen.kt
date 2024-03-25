package com.example.program0201roomdb

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ContactScreen(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvent(ContactEvent.ShowDialogue)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add contact")
            }
        }
    ) { padding -> /*padding ->*/
        padding
        if (state.isAddingContact) {
            AddContactDialogue(state = state, onEvent = onEvent)
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SortType.entries.forEach { sortType ->
                        Row(
                            modifier = Modifier
                                .clickable {
                                    onEvent(ContactEvent.SortContacts(sortType = sortType))
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.sortType == sortType,
                                onClick = {
                                    onEvent(ContactEvent.SortContacts(sortType = sortType))
                                }
                            )
                            Text(text = sortType.name)
                        }
                    }
                }
            }

            items(state.contacts) { contact ->
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        Text(
                            text = "${contact.firstName} ${contact.lastName}",
                            fontSize = 20.sp
                        )
                        Text(
                            text = contact.phoneNumber,
                            fontSize = 14.sp
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                onEvent(ContactEvent.DeleteContact(contact = contact))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete contact"
                            )
                        }
                    }
                }
            }
        }
    }
}