package com.app.blue_jays_go.data.repository

import com.app.blue_jays_go.data.remote.SportsApi
import com.app.blue_jays_go.domain.model.Team
import com.app.blue_jays_go.domain.repository.SportsRepository

// Repository implementation: fetches data from API and maps DTO → domain model
// SportsRepositoryImpl.kt (data)
class SportsRepositoryImpl(
    private val api: SportsApi  // Injected automatically by Koin
) : SportsRepository {

    override suspend fun getTeamDetails(teamSlug: String): Team? {
        return try {
            val response = api.getTeamDetails(teamSlug)
            val dto = response.team

            Team(
                id = dto.id,
                name = dto.displayName,
                abbreviation = dto.abbreviation,
                logoUrl = dto.logos.firstOrNull()?.href ?: "",
                wins = dto.record?.items?.firstOrNull()?.summary?.split("-")?.firstOrNull()?.toIntOrNull() ?: 0,
                losses = dto.record?.items?.firstOrNull()?.summary?.split("-")?.getOrNull(1)?.toIntOrNull() ?: 0,
                nextGame = dto.nextEvent.firstOrNull()?.date
            )
        } catch (e: Exception) {
            null
        }
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

