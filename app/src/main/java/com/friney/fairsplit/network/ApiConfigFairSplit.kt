package com.friney.fairsplit.network

class ApiConfigFairSplit {
    companion object {
        const val BASE_URL = "http://192.168.1.77:8080"

        const val AUTH = "/auth"
        const val REFRESH = "$AUTH/refresh"
        const val LOGIN = "$AUTH/login"
        const val REGISTRATION = "$AUTH/registration"
        const val CHANGE_PASSWORD = "$AUTH/change-password"
        const val USERS = "/users"
        const val EVENTS = "/events"
        const val EVENTS_ID = "/events/{eventId}"
        const val RECEIPTS = "$EVENTS_ID/receipts"
        const val RECEIPTS_ID = "/receipts/{receiptId}"
        const val EXPENSES = "$RECEIPTS_ID/expenses"
        const val EXPENSES_ID = "/expenses/{expenseId}"
        const val EXPENSES_MEMBERS = "$EXPENSES_ID/members"
        const val SUMMARY = "$EVENTS_ID/summary"
    }
}
