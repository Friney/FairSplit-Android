package com.friney.fairsplit.data.repository.receipt

import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.receipt.ReceiptCreate
import com.friney.fairsplit.network.model.receipt.ReceiptUpdate
import retrofit2.Response

interface ReceiptRepository {

    suspend fun getAllByEventId(eventId: Long): Response<List<Receipt>>

    suspend fun create(create: ReceiptCreate, eventId: Long): Response<Receipt>

    suspend fun update(
        receiptUpdate: ReceiptUpdate,
        receiptid: Long,
        eventId: Long
    ): Response<Receipt>

    suspend fun delete(receiptid: Long, eventId: Long)
}