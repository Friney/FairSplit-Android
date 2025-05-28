package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.ExpenseMember
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExpenseMemberService {

    @GET(ApiConfigFairSplit.EXPENSES_MEMBERS)
    suspend fun getAllByExpenseId(@Path("expenseId") expenseId: Long): Response<List<ExpenseMember>>
}