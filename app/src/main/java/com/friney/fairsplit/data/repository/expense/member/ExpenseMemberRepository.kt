package com.friney.fairsplit.data.repository.expense.member

import com.friney.fairsplit.network.model.ExpenseMember
import retrofit2.Response

interface ExpenseMemberRepository {

    suspend fun getAllByExpenseId(expenseId: Long): Response<List<ExpenseMember>>
}