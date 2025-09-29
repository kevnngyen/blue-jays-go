package com.app.data

import EventDto
import RecordDto
import RecordItemDto
import TeamDetailDto
import TeamResponse
import com.app.blue_jays_go.data.dto.*
import com.app.blue_jays_go.data.remote.SportsApi
import com.app.blue_jays_go.data.repository.SportsRepositoryImpl
import com.app.blue_jays_go.domain.model.Team
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SportsRepositoryImplTest {

    private lateinit var repository: SportsRepositoryImpl

    // Fake API that always returns a fixed response
    private val fakeApi = object : SportsApi {

        override suspend fun getTeams(): LeagueResponse {
            return LeagueResponse(
                sports = listOf(
                    Sport(
                        leagues = listOf(
                            League(
                                teams = listOf(
                                    TeamWrapper(
                                        team = TeamDto(
                                            id = "14",
                                            displayName = "Toronto Blue Jays",
                                            abbreviation = "TOR",
                                            logos = listOf(LogoDto("https://logo.png")),
                                        )
                                    ),
                                    TeamWrapper(
                                        team = TeamDto(
                                            id = "23",
                                            displayName = "New York Yankees",
                                            abbreviation = "NYK",
                                            logos = listOf(LogoDto("https://logo.png")),
                                        )
                                    ),
                                    TeamWrapper(
                                        team = TeamDto(
                                            id = "21",
                                            displayName = "Los Angels Dodgers",
                                            abbreviation = "LAD",
                                            logos = listOf(LogoDto("https://logo.png")),
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }


        override suspend fun getTeamDetails(teamName: String): TeamResponse {
            return TeamResponse(
                team = TeamDetailDto(
                    id = "123",
                    displayName = "Toronto Blue Jays",
                    abbreviation = "TOR",
                    logos = listOf(LogoDto("https://logo.png")),
                    record = RecordDto(listOf(RecordItemDto("90-72"))),
                    nextEvent = listOf(EventDto("Yankees vs Blue Jays", "2025-09-30"))
                )
            )
        }
    }

    @Before
    fun setup() {
        repository = SportsRepositoryImpl(fakeApi)
    }

    @Test
    fun getTeamDetailsTest() = runTest {

        val result: Team? = repository.getTeamDetails("tor")

        assertEquals("123", result?.id)
        assertEquals("Toronto Blue Jays", result?.name)
        assertEquals("TOR", result?.abbreviation)
        assertEquals("https://logo.png", result?.logoUrl)
        assertEquals(90, result?.wins)
        assertEquals(72, result?.losses)
        assertEquals("2025-09-30", result?.nextGame)
    }

    @Test
    fun getTeamsTest() = runTest {

        val result: List<String> = repository.getTeams()

        val expectedResults = mutableListOf("TOR", "NYK", "LAD")

        for (teamAbr in result) {

            assertTrue("$teamAbr DNE within the expected results",expectedResults.contains(teamAbr),)
        }
    }

    @Test
    fun getTeamsEmptyResponseTest() = runTest {

        // Fake API that returns an empty response
        val emptyApi = object : SportsApi {
            override suspend fun getTeams(): LeagueResponse {
                return LeagueResponse(
                    sports = emptyList()
                )
            }

            // When you get a request for an invalid team
            // you get hit with {"code":400,"message":"Failed to get league teams summary"}
            override suspend fun getTeamDetails(teamName: String): TeamResponse {
                throw RuntimeException("Failed to get league teams summary") // mimic API 400
            }
        }

        val emptyRepository = SportsRepositoryImpl(emptyApi)

        val result: List<String> = emptyRepository.getTeams()

        // Assert that result is an empty list
        assertTrue("Expected empty team list", result.isEmpty())

    }

    @Test
    fun getTeamDetailsApiErrorReturnsNull() = runTest {
        val errorApi = object : SportsApi {
            override suspend fun getTeams(): LeagueResponse = LeagueResponse(emptyList())

            // When you get a request for an invalid team
            // you get hit with {"code":400,"message":"Failed to get league teams summary"}
            override suspend fun getTeamDetails(teamName: String): TeamResponse {
                throw RuntimeException("Failed to get league teams summary") // mimic API 400
            }
        }

        val repo = SportsRepositoryImpl(errorApi)

        val result: Team? = repo.getTeamDetails("fakeTeam")

        assertEquals("Expected null when API throws", null, result)
    }




}
