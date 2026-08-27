package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.receipt.ReceiptCreate
import com.friney.fairsplit.network.model.receipt.ReceiptUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ReceiptService {

    @GET(ApiConfigFairSplit.RECEIPTS)
    suspend fun getAllByEventId(@Path("eventId") eventId: Long): Response<List<Receipt>>

    @POST(ApiConfigFairSplit.RECEIPTS)
    suspend fun create(
        @Body create: ReceiptCreate,
        @Path("eventId") eventId: Long
    ): Response<Receipt>

    @PATCH(ApiConfigFairSplit.RECEIPTS_BY_ID)
    suspend fun update(
        @Body update: ReceiptUpdate,
        @Path("receiptId") receiptId: Long,
        @Path("eventId") eventId: Long
    ): Response<Receipt>

    @DELETE(ApiConfigFairSplit.RECEIPTS_BY_ID)
    suspend fun delete(@Path("receiptId") receiptId: Long, @Path("eventId") eventId: Long)
}