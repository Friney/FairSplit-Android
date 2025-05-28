package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.Expense
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExpenseService {

    @GET(ApiConfigFairSplit.EXPENSES)
    suspend fun getAllByReceiptId(@Path("receiptId") receiptId: Long): Response<List<Expense>>
}