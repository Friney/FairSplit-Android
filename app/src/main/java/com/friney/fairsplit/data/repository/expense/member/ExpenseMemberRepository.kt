package com.friney.fairsplit.data.repository.expense.member

import com.friney.fairsplit.network.model.expense.member.ExpenseMember
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberCreate
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberUpdate
import retrofit2.Response

interface ExpenseMemberRepository {

    suspend fun getAllByExpenseId(expenseId: Long): Response<List<ExpenseMember>>

    suspend fun create(create: ExpenseMemberCreate, expenseId: Long): Response<ExpenseMember>

    suspend fun delete(expenseMemberId: Long, expenseId: Long)

    suspend fun update(
        expenseMemberId: Long,
        expenseId: Long,
        update: ExpenseMemberUpdate
    ): Response<ExpenseMember>
}