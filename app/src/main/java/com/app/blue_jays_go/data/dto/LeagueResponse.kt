package com.app.blue_jays_go.data.dto

data class LeagueResponse(
    val sports: List<SportDto>
)

data class SportDto(
    val leagues: List<LeagueDto>
)

data class LeagueDto(
    val teams: List<TeamWrapperDto>
)

