package com.friney.fairsplit.data.repository.receipt

import com.friney.fairsplit.network.model.receipt.Receipt
import com.friney.fairsplit.network.model.receipt.ReceiptCreate
import com.friney.fairsplit.network.model.receipt.ReceiptUpdate
import com.friney.fairsplit.network.service.ReceiptService
import retrofit2.Response
import javax.inject.Inject

class NetworkReceiptRepository @Inject constructor(private val receiptService: ReceiptService) :
    ReceiptRepository {

    override suspend fun getAllByEventId(eventId: Long) =
        receiptService.getAllByEventId(eventId)

    override suspend fun create(
        create: ReceiptCreate,
        eventId: Long
    ): Response<Receipt> =
        receiptService.create(create, eventId)

    override suspend fun update(
        receiptUpdate: ReceiptUpdate,
        receiptid: Long,
        eventId: Long
    ) = receiptService.update(receiptUpdate, receiptid, eventId)


    override suspend fun delete(receiptid: Long, eventId: Long) =
        receiptService.delete(receiptid, eventId)
}