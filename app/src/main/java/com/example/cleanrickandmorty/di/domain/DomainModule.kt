package com.example.cleanrickandmorty.di.domain

import com.example.cleanrickandmorty.domain.usecases.GetCharacterByIdUseCase
import com.example.cleanrickandmorty.domain.usecases.GetCharacterUseCase
import org.koin.dsl.module

val domainModule = module {
    single {
        GetCharacterUseCase(repository = get())
    }
    single {
        GetCharacterByIdUseCase(repository = get())
    }
}