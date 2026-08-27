package com.friney.fairsplit.network.model.expense

import com.friney.fairsplit.network.model.expense.member.ExpenseMember
import java.io.Serializable
import java.math.BigDecimal

data class Expense(
    val id: Long,
    val amount: BigDecimal,
    val expenseMembers: List<ExpenseMember>,
    val name: String
) : Serializable