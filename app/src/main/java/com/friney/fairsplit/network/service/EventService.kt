package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.Event
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface EventService {

    @GET(ApiConfigFairSplit.EVENTS)
    suspend fun getAllEvent(@Header("Authorization") token: String): Response<List<Event>>
}