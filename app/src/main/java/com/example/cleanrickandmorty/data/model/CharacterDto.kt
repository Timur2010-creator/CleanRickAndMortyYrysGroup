package com.example.cleanrickandmorty.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    @SerialName("info")
    val info: Info,
    @SerialName("results")
    val results: List<Result>
) {
    @Serializable
    data class Info(
        @SerialName("count")
        val count: Int,
        @SerialName("next")
        val next: String,
        @SerialName("pages")
        val pages: Int,
        @SerialName("prev")
        val prev: String?
    )

    @Serializable
    data class Result(
        @SerialName("created")
        val created: String,
        @SerialName("episode")
        val episode: List<String>,
        @SerialName("gender")
        val gender: String,
        @SerialName("id")
        val id: Int,
        @SerialName("image")
        val image: String,
        @SerialName("location")
        val location: Location,
        @SerialName("name")
        val name: String,
        @SerialName("origin")
        val origin: Origin,
        @SerialName("species")
        val species: String,
        @SerialName("status")
        val status: String,
        @SerialName("type")
        val type: String,
        @SerialName("url")
        val url: String
    ) {
        @Serializable
        data class Location(
            @SerialName("name")
            val name: String,
            @SerialName("url")
            val url: String
        )

        @Serializable
        data class Origin(
            @SerialName("name")
            val name: String,
            @SerialName("url")
            val url: String
        )
    }
}