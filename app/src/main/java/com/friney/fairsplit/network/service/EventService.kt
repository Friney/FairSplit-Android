package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.Event
import retrofit2.Response
import retrofit2.http.GET

interface EventService {

    @GET(ApiConfigFairSplit.EVENTS)
    suspend fun getAll(): Response<List<Event>>
}