package com.friney.fairsplit.data.repository.event

import com.friney.fairsplit.network.model.Event
import com.friney.fairsplit.network.model.EventCreate
import com.friney.fairsplit.network.model.EventUpdate
import com.friney.fairsplit.network.service.EventService
import retrofit2.Response
import javax.inject.Inject

class NetworkEventRepository @Inject constructor(private val eventService: EventService) :
    EventRepository {

    override suspend fun getAll() = eventService.getAll()

    override suspend fun delete(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun update(
        eventUpdate: EventUpdate,
        id: Long
    ): Response<Event> {
        TODO("Not yet implemented")
    }

    override suspend fun сreate(eventCreate: EventCreate): Response<Event> {
        TODO("Not yet implemented")
    }
}