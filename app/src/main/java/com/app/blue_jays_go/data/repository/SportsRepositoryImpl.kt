package com.app.blue_jays_go.data.repository

import com.app.blue_jays_go.data.remote.SportsApi
import com.app.blue_jays_go.domain.model.Team
import com.app.blue_jays_go.domain.repository.SportsRepository

// Repository implementation: fetches data from API and maps DTO → domain model
// SportsRepositoryImpl.kt (data)
class SportsRepositoryImpl(
    private val api: SportsApi  // Injected automatically by Koin
) : SportsRepository {

    override suspend fun getTeamDetails(teamSlug: String): Team {
        // simple pass-through for now (DTO in, DTO out)
        val response = api.getTeamDetails(teamSlug)
        return Team( // map DTO → domain model
            id = response.team.id,
            name = response.team.displayName,
            abbreviation = response.team.abbreviation,
            logoUrl = response.team.logos.firstOrNull()?.href ?: "",
            wins = response.team.record?.items?.firstOrNull()?.summary?.split("-")?.firstOrNull()?.toIntOrNull() ?: 0,
            losses = response.team.record?.items?.firstOrNull()?.summary?.split("-")?.getOrNull(1)?.toIntOrNull() ?: 0,
            nextGame = response.team.nextEvent?.firstOrNull()?.date
        )

    }

    // returns list of mlb team abbreviation
    override suspend fun getTeams(): List<String> {

        val response = api.getTeams()
        val mlbTeamsAbr = mutableListOf<String>()
        val baseball = response.sports
        val teamsList = baseball.firstOrNull()?.leagues?.firstOrNull()?.teams

        if (teamsList != null) {
            for (e in teamsList)

                mlbTeamsAbr.add(e.team.abbreviation)
        }

        return mlbTeamsAbr
    }

}

