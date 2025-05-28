package com.friney.fairsplit.data.repository.summary

import com.friney.fairsplit.network.service.SummaryService
import javax.inject.Inject

class NetworkSummaryRepository @Inject constructor(private val summaryService: SummaryService) :
    SummaryRepository {

    override suspend fun getSummaryByEventId(eventId: Long) =
        summaryService.getSummaryByEventId(eventId)
}