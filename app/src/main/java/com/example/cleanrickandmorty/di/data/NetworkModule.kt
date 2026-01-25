package com.example.cleanrickandmorty.di.data

import com.example.cleanrickandmorty.data.datasource.CharacterService
import com.example.cleanrickandmorty.data.repository.CharacterRepositoryImpl
import com.example.cleanrickandmorty.domain.repository.CharacterRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideRetrofit(get()) }
    single { provideOkHttpClient(get()) }
    single<Interceptor> { provideHttpLoggingInterceptor() }
    single { provideAppService(get()) }
    single<CharacterRepository> { CharacterRepositoryImpl(get()) }
}



fun provideRetrofit (
    okHttpClient: OkHttpClient
) : Retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .baseUrl("https://rickandmortyapi.com/api/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun provideOkHttpClient(
    interceptor: Interceptor
) : OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}


fun provideAppService(
    retrofit: Retrofit
) : CharacterService = retrofit.create(CharacterService::class.java)