package com.friney.fairsplit.data.repository.event

import com.friney.fairsplit.network.model.event.Event
import com.friney.fairsplit.network.model.event.EventCreate
import com.friney.fairsplit.network.model.event.EventUpdate
import retrofit2.Response

interface EventRepository {

    suspend fun getAll(): Response<List<Event>>

    suspend fun create(eventCreate: EventCreate): Response<Event>

    suspend fun update(eventUpdate: EventUpdate, id: Long): Response<Event>

    suspend fun delete(id: Long)
}
