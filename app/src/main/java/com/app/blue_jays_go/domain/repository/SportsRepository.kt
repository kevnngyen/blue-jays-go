package com.app.blue_jays_go.domain.repository

import com.app.blue_jays_go.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface SportsRepository {
    suspend fun getTeamDetails(team: String): Team


}
