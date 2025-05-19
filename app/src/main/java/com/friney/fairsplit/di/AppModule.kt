package com.friney.fairsplit.di

import com.friney.fairsplit.data.repository.event.EventRepository
import com.friney.fairsplit.data.repository.event.NetworkEventRepository
import com.friney.fairsplit.data.repository.user.NetworkUserRepository
import com.friney.fairsplit.data.repository.user.UserRepository
import com.friney.fairsplit.network.ApiConfigFairSplit
import com.friney.fairsplit.network.service.EventService
import com.friney.fairsplit.network.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun baseUrl() = ApiConfigFairSplit.BASE_URL

    @Provides
    fun logging() = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(logging())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient())
            .build()

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService =
        retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideEventService(retrofit: Retrofit): EventService =
        retrofit.create(EventService::class.java)


    @Provides
    fun provideUserRepository(userService: UserService): UserRepository =
        NetworkUserRepository(userService)

    @Provides
    fun provideEventRepository(eventService: EventService): EventRepository =
        NetworkEventRepository(eventService)
}