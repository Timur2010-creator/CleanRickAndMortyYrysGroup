package com.example.cleanrickandmorty.domain.usecases

import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.domain.repository.CharacterRepository
import com.example.cleanrickandmorty.util.Either
import kotlinx.coroutines.flow.Flow

class GetCharacterUseCase(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke() : Flow<Either<String, Character>> = repository.getCharacter()
}