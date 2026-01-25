package com.example.cleanrickandmorty.domain.usecases

import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.domain.repository.CharacterRepository
import com.example.cleanrickandmorty.util.Either
import kotlinx.coroutines.flow.Flow

class GetCharacterByIdUseCase (
    private val repository : CharacterRepository
) {
    suspend operator fun invoke(id : Int): Flow<Either<String, Character.Result>> = repository.getCharacterById(id)
}