package com.example.cleanrickandmorty.di.presentation

import com.example.cleanrickandmorty.presentation.activity.DetailViewModel
import com.example.cleanrickandmorty.presentation.activity.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        MainViewModel(
            getCharacterUseCase = get()
        )
    }
    viewModel {
        DetailViewModel(
            getCharacterByIdUseCase = get()
        )
    }
}