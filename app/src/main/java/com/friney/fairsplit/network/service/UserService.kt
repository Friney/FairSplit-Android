package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.User
import retrofit2.Response
import retrofit2.http.GET

interface UserService {

    @GET(ApiConfigFairSplit.USERS)
    suspend fun getAllUser(): Response<List<User>>
}