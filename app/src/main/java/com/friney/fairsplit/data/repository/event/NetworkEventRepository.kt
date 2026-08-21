package com.friney.fairsplit.data.repository.event

import com.friney.fairsplit.network.model.event.EventCreate
import com.friney.fairsplit.network.model.event.EventUpdate
import com.friney.fairsplit.network.service.EventService
import javax.inject.Inject

class NetworkEventRepository @Inject constructor(private val eventService: EventService) :
    EventRepository {

    override suspend fun getAll() = eventService.getAll()

    override suspend fun create(eventCreate: EventCreate) = eventService.create(eventCreate)

    override suspend fun update(eventUpdate: EventUpdate, id: Long) =
        eventService.update(eventUpdate, id)

    override suspend fun delete(id: Long) = eventService.delete(id)
}