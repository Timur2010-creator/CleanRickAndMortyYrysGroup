package com.example.cleanrickandmorty.data.mapper

import com.example.cleanrickandmorty.data.model.CharacterDto
import com.example.cleanrickandmorty.domain.model.Character

fun CharacterDto.toCharacter() : Character = Character (
    results = this.results.map { it.toResult() }
)

fun CharacterDto.Result.toResult() : Character.Result = Character.Result(
    image = this.image,
    name = this.name,
    id = this.id,
    episode = this.episode
)