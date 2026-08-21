package com.friney.fairsplit.data.repository.expense

import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.ExpenseCreate
import com.friney.fairsplit.network.model.expense.ExpenseUpdate
import retrofit2.Response

interface ExpenseRepository {

    suspend fun getAllByReceiptId(receiptId: Long): Response<List<Expense>>

    suspend fun create(create: ExpenseCreate, receiptId: Long): Response<Expense>

    suspend fun update(
        expenseUpdate: ExpenseUpdate,
        expenseId: Long,
        receiptId: Long
    ): Response<Expense>

    suspend fun delete(expenseId: Long, receiptId: Long)
}