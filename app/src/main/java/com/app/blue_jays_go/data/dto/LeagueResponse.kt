package com.app.blue_jays_go.data.dto

data class LeagueResponse(
    val sports: List<Sport>
)

data class Sport(
    val leagues: List<League>
)

data class League(
    val teams: List<TeamWrapper>
)

data class TeamWrapper(
    val team: TeamDto
)

