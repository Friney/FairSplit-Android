package com.friney.fairsplit.network.service

import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.model.CreateRegisteredUser
import com.friney.fairsplit.network.model.JwtAuthentication
import com.friney.fairsplit.network.model.RefreshToken
import com.friney.fairsplit.network.model.RegisteredUser
import com.friney.fairsplit.network.model.UserCredentials
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @POST(ApiConfigFairSplit.LOGIN)
    suspend fun login(@Body credentials: UserCredentials): Response<JwtAuthentication>

    @POST(ApiConfigFairSplit.REGISTRATION)
    suspend fun register(@Body user: CreateRegisteredUser): Response<RegisteredUser>

    @POST(ApiConfigFairSplit.REFRESH)
    suspend fun refresh(@Body refreshToken: RefreshToken): Response<JwtAuthentication>

    @GET(ApiConfigFairSplit.ME)
    suspend fun getCurrentUser(): Response<RegisteredUser>
}