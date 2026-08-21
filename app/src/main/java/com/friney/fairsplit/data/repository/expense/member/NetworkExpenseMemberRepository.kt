package com.friney.fairsplit.data.repository.expense.member

import com.friney.fairsplit.network.model.expense.member.ExpenseMemberCreate
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberUpdate
import com.friney.fairsplit.network.service.ExpenseMemberService
import javax.inject.Inject

class NetworkExpenseMemberRepository @Inject constructor(private val expenseMemberService: ExpenseMemberService) :
    ExpenseMemberRepository {

    override suspend fun getAllByExpenseId(expenseId: Long) =
        expenseMemberService.getAllByExpenseId(expenseId)

    override suspend fun create(
        create: ExpenseMemberCreate,
        expenseId: Long
    ) = expenseMemberService.create(create, expenseId)

    override suspend fun update(
        update: ExpenseMemberUpdate,
        expenseMemberId: Long,
        expenseId: Long
    ) = expenseMemberService.update(update, expenseMemberId, expenseId)

    override suspend fun delete(expenseMemberId: Long, expenseId: Long) =
        expenseMemberService.delete(expenseMemberId, expenseId)

}
