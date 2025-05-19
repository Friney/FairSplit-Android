package com.friney.fairsplit.data.repository.event

import com.friney.fairsplit.network.model.Event
import com.friney.fairsplit.network.model.EventCreate
import com.friney.fairsplit.network.model.EventUpdate
import retrofit2.Response

interface EventRepository {

    suspend fun getAll(): Response<List<Event>>

    suspend fun сreateEvent(eventCreate: EventCreate): Response<Event>

    suspend fun updateEvent(eventUpdate: EventUpdate, id: Long): Response<Event>

    suspend fun deleteEvent(id: Long)

}
