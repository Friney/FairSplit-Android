package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.summary.Summary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SummaryService {

    @GET(ApiConfigFairSplit.SUMMARY)
    suspend fun getSummaryByEventId(@Path("eventId") eventId: Long): Response<Summary>
}