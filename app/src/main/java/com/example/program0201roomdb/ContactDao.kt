package com.example.program0201roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Upsert
    suspend fun upsertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("SELECT * FROM Contact ORDER BY firstName")
    fun getContactsOrderedByFirstName(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact ORDER BY lastName")
    fun getContactsOrderedByLastName(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact ORDER BY phoneNumber")
    fun getContactsOrderedByPhoneNumber(): Flow<List<Contact>>
}