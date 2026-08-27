package com.friney.fairsplit.network.model.expense

import java.io.Serializable
import java.math.BigDecimal

data class ExpenseUpdate(
    val amount: BigDecimal,
    val name: String
) : Serializable