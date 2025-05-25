package com.friney.fairsplit.data.repository.expense

import com.friney.fairsplit.network.model.Expense
import retrofit2.Response

interface ExpenseRepository {

    suspend fun getAllByReceiptId(receiptId: Long): Response<List<Expense>>
}