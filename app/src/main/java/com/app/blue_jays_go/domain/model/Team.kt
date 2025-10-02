package com.app.blue_jays_go.domain.model

data class Team(
    val id: String,
    val name: String,
    val abbreviation: String,
    val logoUrl: String,
    val wins: Int,
    val losses: Int,
    val nextGame: String?, // ISO date or null
    val color: String,
)
