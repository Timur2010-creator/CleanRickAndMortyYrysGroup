package com.example.cleanrickandmorty.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.domain.usecases.GetCharacterUseCase
import com.example.cleanrickandmorty.util.Either
import com.example.cleanrickandmorty.util.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val getCharacterUseCase: GetCharacterUseCase
) : ViewModel() {

    private val _characterState = MutableStateFlow<UIState<Character>>(UIState.Empty())
    val characterState = _characterState.asStateFlow()

    fun getCharacter() {
        viewModelScope.launch {
            getCharacterUseCase().collect { data ->
                _characterState.value = UIState.Loading()
                when (data) {
                    is Either.Left -> {
                        _characterState.value = UIState.Error(data.value)
                    }
                    is Either.Right -> {
                        _characterState.value = UIState.Success(data.value)
                    }
                }
            }
        }
    }
}