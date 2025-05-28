package com.friney.fairsplit.data.repository.summary

import com.friney.fairsplit.network.model.summary.Summary
import retrofit2.Response

interface SummaryRepository {

    suspend fun getSummaryByEventId(eventId: Long): Response<Summary>
}