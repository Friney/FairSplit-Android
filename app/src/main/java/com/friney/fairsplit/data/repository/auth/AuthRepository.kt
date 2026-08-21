package com.friney.fairsplit.data.repository.auth

import com.friney.fairsplit.network.model.token.JwtAuthentication
import com.friney.fairsplit.network.model.user.RegisteredUser
import retrofit2.Response

interface AuthRepository {

    suspend fun login(email: String, password: String): Response<JwtAuthentication>

    suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Response<RegisteredUser>

    suspend fun getCurrentUser(): Response<RegisteredUser>

    suspend fun refreshToken(): Response<Boolean>

    fun logout()

    fun isLoggedIn(): Boolean
}