package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.event.Event
import com.friney.fairsplit.network.model.event.EventCreate
import com.friney.fairsplit.network.model.event.EventUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {

    @GET(ApiConfigFairSplit.EVENTS)
    suspend fun getAll(): Response<List<Event>>

    @POST(ApiConfigFairSplit.EVENTS)
    suspend fun create(@Body create: EventCreate): Response<Event>

    @PATCH(ApiConfigFairSplit.EVENTS_BY_ID)
    suspend fun update(@Body update: EventUpdate, @Path("eventId") id: Long): Response<Event>

    @DELETE(ApiConfigFairSplit.EVENTS_BY_ID)
    suspend fun delete(@Path("eventId") id: Long)
}