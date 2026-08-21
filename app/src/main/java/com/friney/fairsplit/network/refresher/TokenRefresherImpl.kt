package com.friney.fairsplit.network.refresher

import android.util.Log
import com.friney.fairsplit.data.repository.auth.AuthRepository
import com.friney.fairsplit.data.utility.TokenManager
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenRefresherImpl @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: Provider<AuthRepository>
) : TokenRefresher {

    override suspend fun refreshToken(): Boolean {
        Log.i("TokenRefresher", "Try refreshing token")
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken == null) {
            Log.e("TokenRefresher", "Refresh token is null")
            return false
        }

        val response = authRepository.get().refreshToken()
        if (response.isSuccessful) {
            Log.i("TokenRefresher", "Token refreshed successfully")
        } else {
            Log.e("TokenRefresher", "Token refresh failed: ${response.message()}")
        }

        return response.isSuccessful
    }
}
