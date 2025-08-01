package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.ExpenseCreate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExpenseService {

    @GET(ApiConfigFairSplit.EXPENSES)
    suspend fun getAllByReceiptId(@Path("receiptId") receiptId: Long): Response<List<Expense>>

    @POST(ApiConfigFairSplit.EXPENSES)
    suspend fun create(
        @Body create: ExpenseCreate,
        @Path("receiptId") receiptId: Long
    ): Response<Expense>
}