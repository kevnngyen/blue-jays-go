package com.app.blue_jays_go.domain.repository

import com.app.blue_jays_go.domain.model.Team

interface SportsRepository {
    suspend fun getTeamDetails(team: String): Team?

    suspend fun getTeams(): List<String>

}
