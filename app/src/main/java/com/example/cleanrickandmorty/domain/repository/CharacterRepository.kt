package com.example.cleanrickandmorty.domain.repository

import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.util.Either
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun getCharacter() : Flow<Either<String, Character>>
    suspend fun getCharacterById(id : Int) : Flow<Either<String, Character.Result>>
}