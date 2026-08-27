package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.expense.member.ExpenseMember
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberCreate
import com.friney.fairsplit.network.model.expense.member.ExpenseMemberUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ExpenseMemberService {

    @GET(ApiConfigFairSplit.EXPENSES_MEMBERS)
    suspend fun getAllByExpenseId(@Path("expenseId") expenseId: Long): Response<List<ExpenseMember>>

    @POST(ApiConfigFairSplit.EXPENSES_MEMBERS)
    suspend fun create(
        @Body create: ExpenseMemberCreate,
        @Path("expenseId") expenseId: Long
    ): Response<ExpenseMember>

    @PATCH(ApiConfigFairSplit.EXPENSE_MEMBER_BY_ID)
    suspend fun update(
        @Body update: ExpenseMemberUpdate,
        @Path("expenseMemberId") expenseMemberId: Long,
        @Path("expenseId") expenseId: Long
    ): Response<ExpenseMember>

    @DELETE(ApiConfigFairSplit.EXPENSE_MEMBER_BY_ID)
    suspend fun delete(
        @Path("expenseMemberId") expenseMemberId: Long,
        @Path("expenseId") expenseId: Long
    )
}