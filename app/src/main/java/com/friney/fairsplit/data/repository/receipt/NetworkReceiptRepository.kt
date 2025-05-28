package com.friney.fairsplit.data.repository.receipt

import com.friney.fairsplit.network.service.ReceiptService
import javax.inject.Inject

class NetworkReceiptRepository @Inject constructor(private val receiptService: ReceiptService) :
    ReceiptRepository {

    override suspend fun getAllByEventId(eventId: Long) =
        receiptService.getAllByEventId(eventId)
}