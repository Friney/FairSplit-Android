package com.friney.fairsplit.data.repository.auth

import com.friney.fairsplit.network.model.RegisteredUser
import com.friney.fairsplit.network.service.AuthService
import retrofit2.Response
import javax.inject.Inject


class NetworkAuthRepository @Inject constructor(private val authService: AuthService) :
    AuthRepository {

    override fun getUserInApp(): Response<RegisteredUser> {
        val user = RegisteredUser(1, "Test User", "Test Email", "Test Display Name")
        return Response.success(user)
    }
}