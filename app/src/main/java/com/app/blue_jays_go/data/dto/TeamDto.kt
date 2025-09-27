package com.app.blue_jays_go.data.dto

data class TeamDto(
    val id: String,
    val displayName: String,
    val abbreviation: String,
    val logos: List<LogoDto>
)

