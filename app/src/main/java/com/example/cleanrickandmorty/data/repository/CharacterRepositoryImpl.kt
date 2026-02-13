package com.example.cleanrickandmorty.data.repository

import android.util.Log
import com.example.cleanrickandmorty.data.datasource.CharacterService
import com.example.cleanrickandmorty.data.mapper.toCharacter
import com.example.cleanrickandmorty.data.mapper.toResult
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.domain.repository.CharacterRepository
import com.example.cleanrickandmorty.util.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.Response
import okio.IOException

class CharacterRepositoryImpl (
    private val service : CharacterService
) : CharacterRepository {
    override suspend fun getCharacter() : Flow<Either<String, Character>> = flow {
        try {
            val response = service.getCharacter()
            Log.e("ololo", response.toCharacter().toString())
            emit(Either.Right(response.toCharacter()))
        } catch (e : IOException) {
            emit(Either.Left(e.localizedMessage ?: "Unknown error!"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCharacterById(id : Int) : Flow<Either<String, Character.Result>> = flow {
        try {
            val response = service.getCharacterById(id)
            emit(Either.Right(response.toResult()))
        } catch (e : IOException) {
            emit(Either.Left(e.localizedMessage ?: "Unknown error!"))
        }
    }.flowOn(Dispatchers.IO)
}