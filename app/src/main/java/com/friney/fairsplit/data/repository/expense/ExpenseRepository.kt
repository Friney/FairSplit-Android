package com.friney.fairsplit.data.repository.expense

import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.ExpenseCreate
import retrofit2.Response

interface ExpenseRepository {

    suspend fun getAllByReceiptId(receiptId: Long): Response<List<Expense>>

    suspend fun create(create: ExpenseCreate, receiptId: Long): Response<Expense>
}