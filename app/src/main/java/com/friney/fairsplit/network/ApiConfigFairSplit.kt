package com.friney.fairsplit.network

import com.friney.fairsplit.BuildConfig

class ApiConfigFairSplit {
    companion object {
        const val BASE_URL = BuildConfig.FAIR_SPLIT_BASE_URL

        const val API_V1 = "/api/v1"
        const val AUTH = "$API_V1/auth"
        const val ME = "$AUTH/me"
        const val REFRESH = "$AUTH/refresh"
        const val LOGIN = "$AUTH/login"
        const val REGISTRATION = "$AUTH/registration"
        const val CHANGE_PASSWORD = "$AUTH/change-password"
        const val USERS = "$API_V1/users"
        const val EVENTS = "$API_V1/events"
        const val EVENTS_ID = "$EVENTS/{eventId}"
        const val EVENTS_BY_ID = "$EVENTS/{eventId}"
        const val RECEIPTS = "$EVENTS_ID/receipts"
        const val RECEIPTS_ID = "$API_V1/receipts/{receiptId}"
        const val RECEIPTS_BY_ID = "$RECEIPTS/{receiptId}"
        const val EXPENSES = "$RECEIPTS_ID/expenses"
        const val EXPENSES_ID = "$API_V1/expenses/{expenseId}"
        const val EXPENSES_BY_ID = "$EXPENSES/{expenseId}"
        const val EXPENSES_MEMBERS = "$EXPENSES_ID/members"
        const val EXPENSE_MEMBER_BY_ID = "$EXPENSES_MEMBERS/{expenseMemberId}"
        const val SUMMARY = "$EVENTS_ID/summary"
    }
}
