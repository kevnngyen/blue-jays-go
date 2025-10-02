package com.app.blue_jays_go.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.blue_jays_go.domain.model.Team
import com.app.blue_jays_go.domain.repository.SportsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SportsViewModel(
    private val repository: SportsRepository
) : ViewModel() {

    // Backing property: mutable state, only editable inside this ViewModel
    private val _team = MutableStateFlow<Team?>(null)
    private val _teamsAbrv = MutableStateFlow(emptyList<String>())
    private val _selectedTeam = MutableStateFlow<String>("")
    private val _logos = MutableStateFlow<Map<String, Map<String,String?>>>(mutableMapOf())

    // Publicly exposed as read-only so UI can observe
    val team: StateFlow<Team?> = _team
    val teamsAbrv: StateFlow<List<String>> = _teamsAbrv
    val selectedTeam: StateFlow<String> = _selectedTeam
    val logos: StateFlow<Map<String, Map<String, String?>>> = _logos

    // Defaulted selected team
    init {
        _selectedTeam.update { "TOR" }
        loadTeam(_selectedTeam.value)
        loadTeamsAbr()

    }

    // Changes the selected team and updates the detail displayed
    fun changeSelectedTeam(teamSlug: String) {
        viewModelScope.launch {
            try {
                _selectedTeam.update { teamSlug }
                loadTeam(_selectedTeam.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Loads team details (called from UI)
     * Uses coroutines to avoid blocking the main thread
     */
    private fun loadTeam(teamSlug: String) {
        // viewModelScope is a CoroutineScope tied to the ViewModel’s lifecycle
        viewModelScope.launch {
            try {
                // 1. Suspend function call (runs off main thread)
                val result = repository.getTeamDetails(teamSlug)

                // 2. Update StateFlow with result
                //    UI observing this will recompose automatically
                _team.value = result
            } catch (e: Exception) {

                // If API/network fails, we could expose error state here
                e.printStackTrace()
            }
        }
    }

    private fun  loadTeamLogo() {
        viewModelScope.launch (){

            try {

                // Calls a new coroutine scope so all child coroutines (async calls) are tracked together
                val logoMapDetails = coroutineScope {

                    // For each team abbreviation, start an async coroutine
                    _teamsAbrv.value.map { team ->
                        async {
                            // Make the network/database call for this team
                            val result = repository.getTeamDetails(team)

                            // Build a map with the team's logo URL and color
                            val map = mutableMapOf(
                                "url" to (result?.logoUrl ?: ""),
                                "color" to ("#" + (result?.color ?: "000000"))
                            )

                            // Return a Pair of (team -> its data map)
                            team to map // this is should be Pair of <String, Map<String,String>>
                        }
                    }
                        // Wait for all async coroutines to finish (parallel requests)
                        .awaitAll()

                        // Convert the list of Pairs into a Map<String, Map<String, String>>
                        .toMap()
                }


                withContext(Dispatchers.Main) {
                    _logos.value = logoMapDetails
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Loads all team abbreviations from the repository into StateFlow
    fun loadTeamsAbr() {
        viewModelScope.launch {
            try {
                // 1. Call the repository function (suspend) to fetch team abbreviations
                val result = repository.getTeams()

                // 2. Update the private MutableStateFlow with the result
                //    anyone collecting teamsAbrv will see the new list automatically
                _teamsAbrv.value = result

                loadTeamLogo()

            } catch (e: Exception) {

                // 3. If something goes wrong (like network error), print the error
                e.printStackTrace()
            }
        }
    }
}
