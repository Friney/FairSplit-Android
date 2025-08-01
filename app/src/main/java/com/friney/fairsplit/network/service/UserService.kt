package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.user.CreateNotRegisteredUser
import com.friney.fairsplit.network.model.user.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {

    @GET(ApiConfigFairSplit.USERS)
    suspend fun getAllUser(): Response<List<User>>

    @POST(ApiConfigFairSplit.USERS)
    suspend fun createUser(@Body create: CreateNotRegisteredUser): Response<User>
}