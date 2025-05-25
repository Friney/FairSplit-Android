package com.friney.fairsplit.data.repository.expense

import com.friney.fairsplit.network.service.ExpenseService
import javax.inject.Inject

class NetworkExpenseRepository @Inject constructor(private val expenseService: ExpenseService) :
    ExpenseRepository {

    override suspend fun getAllByReceiptId(receiptId: Long) =
        expenseService.getAllByReceiptId(receiptId)
}