package com.example.cleanrickandmorty.domain.model

import com.example.cleanrickandmorty.data.model.CharacterDto.Result.Location

data class Character(
    val results: List<Result>
) {
    data class Result(
        val id : Int,
        val image: String,
        val name: String,
        val status: String,
        val gender: String,
        val species: String,
        val location: Location
    )
}