package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.Receipt
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReceiptService {

    @GET(ApiConfigFairSplit.RECEIPTS)
    suspend fun getAllByEventId(@Path("eventId") eventId: Long): Response<List<Receipt>>
}