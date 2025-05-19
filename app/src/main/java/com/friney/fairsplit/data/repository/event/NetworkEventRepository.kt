package com.friney.fairsplit.data.repository.event

import com.friney.fairsplit.network.service.EventService
import javax.inject.Inject

class NetworkEventRepository @Inject constructor(private val eventService: EventService) :
    EventRepository {

    override
    suspend fun getAllEvent() = eventService.getAllEvent()
}