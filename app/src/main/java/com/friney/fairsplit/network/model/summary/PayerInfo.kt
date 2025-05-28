package com.friney.fairsplit.network.model.summary

import java.math.BigDecimal

data class PayerInfo(
    val total: BigDecimal,
    val user: User
)