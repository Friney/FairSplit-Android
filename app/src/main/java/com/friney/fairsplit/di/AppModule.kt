package com.friney.fairsplit.di

import com.friney.fairsplit.data.repository.auth.AuthRepository
import com.friney.fairsplit.data.repository.auth.NetworkAuthRepository
import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.repository.event.NetworkEventRepository
import com.friney.fairsplit.data.repository.expense.ExpenseRepository
import com.friney.fairsplit.data.repository.expense.NetworkExpenseRepository
import com.friney.fairsplit.data.repository.expense.member.ExpenseMemberRepository
import com.friney.fairsplit.data.repository.expense.member.NetworkExpenseMemberRepository
import com.friney.fairsplit.data.repository.receipt.NetworkReceiptRepository
import com.friney.fairsplit.data.repository.receipt.ReceiptRepository
import com.friney.fairsplit.data.repository.user.NetworkUserRepository
import com.friney.fairsplit.data.repository.user.UserRepository
import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.service.AuthService
import com.friney.fairsplit.network.service.EventService
import com.friney.fairsplit.network.service.ExpenseMemberService
import com.friney.fairsplit.network.service.ExpenseService
import com.friney.fairsplit.network.service.ReceiptService
import com.friney.fairsplit.network.service.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.BigDecimal
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun baseUrl() = ApiConfigFairSplit.BASE_URL

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapter(
                BigDecimal::class.java,
                JsonDeserializer<BigDecimal> { json, _, _ ->
                    when {
                        json.isJsonPrimitive -> json.asBigDecimal
                        else -> throw JsonParseException("Invalid BigDecimal format")
                    }
                })
            .create()
    }

    @Provides
    fun logging() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(logging())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideAuthService(retrofit: Retrofit): AuthService =
        retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideEventService(retrofit: Retrofit): EventService =
        retrofit.create(EventService::class.java)

    @Provides
    @Singleton
    fun provideReceiptService(retrofit: Retrofit): ReceiptService =
        retrofit.create(ReceiptService::class.java)

    @Provides
    @Singleton
    fun provideExpenseService(retrofit: Retrofit): ExpenseService =
        retrofit.create(ExpenseService::class.java)

    @Provides
    @Singleton
    fun provideExpenseMemberService(retrofit: Retrofit): ExpenseMemberService =
        retrofit.create(ExpenseMemberService::class.java)

    @Provides
    fun provideUserRepository(userService: UserService): UserRepository =
        NetworkUserRepository(userService)

    @Provides
    fun provideEventRepository(eventService: EventService): EventRepository =
        NetworkEventRepository(eventService)

    @Provides
    @Singleton
    fun provideAuthRepository(authService: AuthService): AuthRepository =
        NetworkAuthRepository(authService)

    @Provides
    @Singleton
    fun provideReceiptRepository(receiptService: ReceiptService): ReceiptRepository =
        NetworkReceiptRepository(receiptService)

    @Provides
    @Singleton
    fun provideExpenseRepository(expenseService: ExpenseService): ExpenseRepository =
        NetworkExpenseRepository(expenseService)

    @Provides
    @Singleton
    fun provideExpenseMemberRepository(expenseService: ExpenseMemberService): ExpenseMemberRepository =
        NetworkExpenseMemberRepository(expenseService)

}