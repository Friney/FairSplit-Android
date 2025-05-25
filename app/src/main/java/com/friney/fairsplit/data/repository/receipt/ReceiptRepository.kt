package com.friney.fairsplit.data.repository.receipt

import com.friney.fairsplit.network.model.Receipt
import retrofit2.Response

interface ReceiptRepository {

    suspend fun getAllByEventId(eventId: Long): Response<List<Receipt>>
}