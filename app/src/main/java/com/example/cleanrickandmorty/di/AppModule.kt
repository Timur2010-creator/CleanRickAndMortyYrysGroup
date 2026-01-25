package com.example.cleanrickandmorty.di

import com.example.cleanrickandmorty.di.data.networkModule
import com.example.cleanrickandmorty.di.domain.domainModule
import com.example.cleanrickandmorty.di.presentation.presentationModule
val appModule =
    listOf( domainModule, presentationModule, networkModule)