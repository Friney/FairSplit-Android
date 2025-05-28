package com.friney.fairsplit.data.repository.user

import com.friney.fairsplit.network.service.UserService
import javax.inject.Inject

class NetworkUserRepository @Inject constructor(private val userService: UserService) :
    UserRepository {

    override
    suspend fun getAllUser() = userService.getAllUser()
}