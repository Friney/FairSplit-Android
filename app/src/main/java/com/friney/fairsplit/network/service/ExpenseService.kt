package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.expense.Expense
import com.friney.fairsplit.network.model.expense.ExpenseCreate
import com.friney.fairsplit.network.model.expense.ExpenseUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @PATCH(ApiConfigFairSplit.EXPENSES_BY_ID)
    suspend fun update(
        @Body create: ExpenseUpdate,
        @Path("expenseId") expenseId: Long,
        @Path("receiptId") receiptId: Long
    ): Response<Expense>

    @DELETE(ApiConfigFairSplit.EXPENSES_BY_ID)
    suspend fun delete(@Path("expenseId") expenseId: Long, @Path("receiptId") receiptId: Long)
}