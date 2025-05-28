package com.friney.fairsplit.network.model.summary

import java.math.BigDecimal

data class Debt(
    val amount: BigDecimal,
    val from: User,
    val to: User
)