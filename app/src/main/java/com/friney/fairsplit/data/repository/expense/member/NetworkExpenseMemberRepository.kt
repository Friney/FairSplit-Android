package com.friney.fairsplit.data.repository.expense.member

import com.friney.fairsplit.network.service.ExpenseMemberService
import javax.inject.Inject

class NetworkExpenseMemberRepository @Inject constructor(private val expenseMemberService: ExpenseMemberService) :
    ExpenseMemberRepository {

    override suspend fun getAllByExpenseId(expenseId: Long) =
        expenseMemberService.getAllByExpenseId(expenseId)
}