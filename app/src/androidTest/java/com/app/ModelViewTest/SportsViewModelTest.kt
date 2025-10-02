package com.app.ModelViewTest

import com.app.blue_jays_go.domain.model.Team
import com.app.blue_jays_go.domain.repository.SportsRepository
import com.app.blue_jays_go.presentation.ViewModel.SportsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue


/**
 *
 * This test class verifies if the ViewModel correctly talks to repository and updates UI state.
 *
 */

@OptIn(ExperimentalCoroutinesApi::class)
class SportsViewModelTest {

    private lateinit var fakeRepo: FakeSportsRepository
    private lateinit var viewModel: SportsViewModel

    @Before
    fun setup() {
        fakeRepo = FakeSportsRepository()
        viewModel = SportsViewModel(fakeRepo)
    }

    @Test
    fun `the defaulted state when user runs the app should be TOR`() = runTest {
        assertEquals("TOR", viewModel.selectedTeam.value)
        assertEquals("Toronto Blue Jays", viewModel.team.value?.name)
    }

    @Test
    fun `loadTeamsAbr should populate abbreviations`() = runTest {
        viewModel.loadTeamsAbr()
        assertEquals(listOf("TOR", "NYK", "LAD"), viewModel.teamsAbrv.value)
    }

    @Test
    fun `changeSelectedTeam should update and load`() = runTest {
        viewModel.changeSelectedTeam("NYK")
        assertEquals("NYK", viewModel.selectedTeam.value)
        assertEquals("New York Yankees", viewModel.team.value?.name)
    }

    @Test
    fun `repository error should keep state safe`() = runTest {
        fakeRepo.shouldThrow = true
        viewModel.loadTeamsAbr()
        assertTrue(viewModel.teamsAbrv.value.isEmpty())
    }
}

/**
 * We created a fake repository for testing purpose:
 */
class FakeSportsRepository : SportsRepository {
    var shouldThrow = false

    override suspend fun getTeams(): List<String> {
        // TODO I need to add in an exception thrower to the ViewModel
        if (shouldThrow) throw RuntimeException("API error")
        return listOf("TOR", "NYK", "LAD")
    }

    override suspend fun getTeamDetails(teamSlug: String): Team? {
        // TODO I need to add in an exception thrower to the ViewModel
        if (shouldThrow) throw RuntimeException("API error")
        return when (teamSlug) {
            "TOR" -> Team("1", "Toronto Blue Jays", "TOR", "logo", 90, 72, "2025-09-30", "#0000")
            "NYK" -> Team("2", "New York Yankees", "NYK", "logo", 95, 67, "2025-09-30", "#0000")
            else -> null
        }
    }
}
