package com.friney.fairsplit.network.interceptor

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.friney.fairsplit.data.utility.TokenManager
import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.refresher.TokenRefresher
import com.friney.fairsplit.ui.navigation.FragmentNavigator
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val tokenRefresher: TokenRefresher,
    private val fragmentNavigator: FragmentNavigator
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        if (isAuthRequest(originalRequest.url.toString())) {
            return chain.proceed(originalRequest)
        }
//        authNavigator.navigateToLogin()

        val token = tokenManager.getAccessToken()
        if (token != null) {
            var response = proceedRequestWithToken(chain, token)
            Log.i("AuthInterceptor", "Response code: ${response.code}")

            if (response.code == 403) {
                val refreshResult = runBlocking { tokenRefresher.refreshToken() }

                if (refreshResult) {
                    val token = tokenManager.getAccessToken()
                    if (token != null) {
                        response = proceedRequestWithToken(chain, token)
                    }
                }
            }

            if (response.code == 403) {
                tokenManager.clearAll()
                Log.i("AuthInterceptor", "Token expired")
                Handler(Looper.getMainLooper()).post {
                    fragmentNavigator.navigateToLogin()
                }
            }

            return response
        }

        return chain.proceed(originalRequest)
    }

    private fun proceedRequestWithToken(chain: Interceptor.Chain, token: String): Response {
        val newRequest = chain.request().newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }

    private fun isAuthRequest(url: String): Boolean {
        return url.contains(ApiConfigFairSplit.LOGIN) ||
                url.contains(ApiConfigFairSplit.REGISTRATION) ||
                url.contains(ApiConfigFairSplit.REFRESH)
    }
}