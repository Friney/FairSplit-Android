package com.friney.fairsplit.data.repository.receipt

import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.receipt.ReceiptCreate
import retrofit2.Response

interface ReceiptRepository {

    suspend fun getAllByEventId(eventId: Long): Response<List<Receipt>>

    suspend fun create(create: ReceiptCreate, eventId: Long): Response<Receipt>
}