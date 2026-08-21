package com.friney.fairsplit.data.repository.user

import com.friney.fairsplit.network.model.user.CreateNotRegisteredUser
import com.friney.fairsplit.network.model.user.User
import retrofit2.Response

interface UserRepository {

    suspend fun getAllUser(): Response<List<User>>

    suspend fun create(create: CreateNotRegisteredUser): Response<User>
}