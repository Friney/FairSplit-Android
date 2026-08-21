package com.friney.fairsplit.data.repository.expense

import com.friney.fairsplit.network.model.expense.ExpenseCreate
import com.friney.fairsplit.network.model.expense.ExpenseUpdate
import com.friney.fairsplit.network.service.ExpenseService
import javax.inject.Inject

class NetworkExpenseRepository @Inject constructor(private val expenseService: ExpenseService) :
    ExpenseRepository {

    override suspend fun getAllByReceiptId(receiptId: Long) =
        expenseService.getAllByReceiptId(receiptId)

    override suspend fun create(
        create: ExpenseCreate,
        receiptId: Long
    ) = expenseService.create(create, receiptId)

    override suspend fun update(
        expenseUpdate: ExpenseUpdate,
        expenseId: Long,
        receiptId: Long
    ) = expenseService.update(expenseUpdate, expenseId, receiptId)


    override suspend fun delete(expenseId: Long, receiptId: Long) =
        expenseService.delete(expenseId, receiptId)

}