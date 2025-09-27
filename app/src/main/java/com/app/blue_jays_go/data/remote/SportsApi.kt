package com.app.blue_jays_go.data.remote
import TeamResponse
import com.app.blue_jays_go.data.dto.LeagueResponse
import retrofit2.http.GET
import retrofit2.http.Path

// This interface class define the functions to call the API
interface SportsApi {

    @GET("apis/site/v2/sports/baseball/mlb/teams")
    suspend fun getTeams(): LeagueResponse

    // Get details for a specific team
    @GET("apis/site/v2/sports/baseball/mlb/teams/{team}")
    suspend fun getTeamDetails(@Path("team") teamName: String): TeamResponse


}