package com.friney.fairsplit.data.repository.auth

import com.friney.fairsplit.data.utility.TokenManager
import com.friney.fairsplit.network.model.CreateRegisteredUser
import com.friney.fairsplit.network.model.JwtAuthentication
import com.friney.fairsplit.network.model.RefreshToken
import com.friney.fairsplit.network.model.RegisteredUser
import com.friney.fairsplit.network.model.UserCredentials
import com.friney.fairsplit.network.service.AuthService
import retrofit2.Response
import javax.inject.Inject

class NetworkAuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Response<JwtAuthentication> {
        val credentials = UserCredentials(email, password)
        val response = authService.login(credentials)

        if (response.isSuccessful) {
            response.body()?.let { jwtAuth ->
                tokenManager.saveTokens(jwtAuth.token, jwtAuth.refreshToken)
            }
        }

        return response
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Response<RegisteredUser> {
        val user = CreateRegisteredUser(name, email, password, confirmPassword)
        val response = authService.register(user)

        if (response.isSuccessful) {
            response.body()?.let { registeredUser ->
                authService.login(UserCredentials(email, password))
            }
        }

        return response
    }

    override suspend fun getCurrentUser(): Response<RegisteredUser> {
        return authService.getCurrentUser()
    }

    override suspend fun refreshToken(): Response<Boolean> {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken != null) {
            val response = authService.refresh(RefreshToken(refreshToken))
            if (response.isSuccessful) {
                response.body()?.let { jwtAuth ->
                    tokenManager.saveTokens(jwtAuth.token, jwtAuth.refreshToken)
                    return Response.success(true)
                }
            }
        }
        return Response.success(false)
    }

    override fun logout() {
        tokenManager.clearAll()
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}